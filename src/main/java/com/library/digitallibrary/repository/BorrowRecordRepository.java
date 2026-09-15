package com.library.digitallibrary.repository;

import com.library.digitallibrary.entity.BorrowRecord;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUser(User user);

    List<BorrowRecord> findByBook(Book book);

    List<BorrowRecord> findByStatus(String status);
    
    List<BorrowRecord> findByFineAmountGreaterThan(double amount);
}