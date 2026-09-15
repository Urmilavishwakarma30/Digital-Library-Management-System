package com.library.digitallibrary.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.library.digitallibrary.entity.Book;
import com.library.digitallibrary.entity.BorrowRecord;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.repository.BookRepository;
import com.library.digitallibrary.repository.BorrowRecordRepository;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         BookRepository bookRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
    }

    // Issue book
    public BorrowRecord issueBook(User user, Book book) {

        // Check whether the same user already has this book issued
        List<BorrowRecord> userRecords =
                borrowRecordRepository.findByUser(user);

        for (BorrowRecord record : userRecords) {

            if (record.getBook().getId().equals(book.getId())
                    && "ISSUED".equals(record.getStatus())) {

                throw new RuntimeException(
                        "You have already issued this book"
                );
            }
        }

        // Check book availability
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        // Decrease available quantity
        book.setAvailableQuantity(
                book.getAvailableQuantity() - 1
        );

        bookRepository.save(book);

        // Create borrow record
        BorrowRecord record = new BorrowRecord();

        record.setUser(user);
        record.setBook(book);

        record.setIssueDate(LocalDate.now());

        // Due date = 14 days after issue
        record.setDueDate(
                LocalDate.now().plusDays(14)
        );

        record.setStatus("ISSUED");

        record.setFineAmount(0);

        record.setFinePaid(true);

        return borrowRecordRepository.save(record);
    }

    // Return book
    public BorrowRecord returnBook(Long borrowId) {

        BorrowRecord record =
                borrowRecordRepository.findById(borrowId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Borrow record not found"
                    )
                );

        // Check if already returned
        if ("RETURNED".equals(record.getStatus())) {

            throw new RuntimeException(
                    "Book already returned"
            );
        }

        LocalDate returnDate = LocalDate.now();

        record.setReturnDate(returnDate);
        record.setStatus("RETURNED");

        // Increase available quantity
        Book book = record.getBook();

        book.setAvailableQuantity(
                book.getAvailableQuantity() + 1
        );

        bookRepository.save(book);

        // Calculate late fine
        if (returnDate.isAfter(record.getDueDate())) {

            long lateDays = ChronoUnit.DAYS.between(
                    record.getDueDate(),
                    returnDate
            );

            double fine = lateDays * 5.0;

            record.setFineAmount(fine);

            record.setFinePaid(false);

        } else {

            record.setFineAmount(0);

            record.setFinePaid(true);
        }

        return borrowRecordRepository.save(record);
    }

    // Get user's borrow history
    public List<BorrowRecord> getUserBorrowRecords(User user) {

        return borrowRecordRepository.findByUser(user);
    }

    // Get all currently issued books
    public List<BorrowRecord> getIssuedBooks() {

        return borrowRecordRepository.findByStatus("ISSUED");
    }
    
 // Get all records having fine
    public List<BorrowRecord> getFineRecords() {

        List<BorrowRecord> allRecords = borrowRecordRepository.findAll();

        LocalDate today = LocalDate.now();

        for (BorrowRecord record : allRecords) {

            if (record.getDueDate() != null
                    && record.getDueDate().isBefore(today)
                    && "ISSUED".equals(record.getStatus())) {

                long lateDays = ChronoUnit.DAYS.between(
                        record.getDueDate(),
                        today
                );

                double fine = lateDays * 5.0;

                record.setFineAmount(fine);
                record.setFinePaid(false);

                borrowRecordRepository.save(record);
            }
        }

        return borrowRecordRepository.findByFineAmountGreaterThan(0);
    }
 // Mark fine as paid
    public BorrowRecord markFinePaid(Long borrowId) {

        BorrowRecord record = borrowRecordRepository.findById(borrowId)
                .orElseThrow(() ->
                    new RuntimeException("Borrow record not found")
                );

        if (record.getFineAmount() <= 0) {
            throw new RuntimeException("No fine exists for this record");
        }

        record.setFinePaid(true);

        return borrowRecordRepository.save(record);
    }

    // Get all borrow records
    public List<BorrowRecord> getAllRecords() {

        return borrowRecordRepository.findAll();
    }
}