package com.library.digitallibrary.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.library.digitallibrary.entity.Book;
import com.library.digitallibrary.entity.BorrowRecord;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.repository.BookRepository;
import com.library.digitallibrary.repository.UserRepository;
import com.library.digitallibrary.service.BorrowService;

@Controller
public class BorrowController {

    private final BorrowService borrowService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BorrowController(BorrowService borrowService,
                            UserRepository userRepository,
                            BookRepository bookRepository) {

        this.borrowService = borrowService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // Issue book
    @PostMapping("/books/issue")
    public String issueBook(@RequestParam Long bookId,
                            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        try {

            borrowService.issueBook(user, book);

            return "redirect:/books?issued";

        } catch (RuntimeException e) {

            if ("You have already issued this book".equals(e.getMessage())) {
                return "redirect:/books?alreadyIssued";
            }

            if ("Book is not available".equals(e.getMessage())) {
                return "redirect:/books?notAvailable";
            }

            return "redirect:/books?error";
        }
    }

    // User borrow history
    @GetMapping("/my-books")
    public String myBooks(Authentication authentication,
                           Model model) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<BorrowRecord> records =
                borrowService.getUserBorrowRecords(user);

        model.addAttribute("records", records);

        return "my-books";
    }

    // Return book
    @PostMapping("/books/return")
    public String returnBook(@RequestParam Long borrowId) {

        borrowService.returnBook(borrowId);

        return "redirect:/my-books";
    }
}