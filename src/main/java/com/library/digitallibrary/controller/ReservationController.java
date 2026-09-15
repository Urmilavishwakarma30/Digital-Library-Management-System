package com.library.digitallibrary.controller;

import com.library.digitallibrary.entity.Book;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.repository.BookRepository;
import com.library.digitallibrary.repository.UserRepository;
import com.library.digitallibrary.service.ReservationService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReservationController {

    private final ReservationService reservationService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService,
                                  BookRepository bookRepository,
                                  UserRepository userRepository) {
        this.reservationService = reservationService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/books/reserve")
    public String reserveBook(@RequestParam Long bookId,
                              Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        reservationService.reserveBook(user, book);

        return "redirect:/reservations";
    }

    @GetMapping("/reservations")
    public String myReservations(Authentication authentication, Model model) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute(
                "reservations",
                reservationService.getUserReservations(user)
        );

        return "reservations";
    }

    @PostMapping("/reservations/cancel")
    public String cancelReservation(@RequestParam Long id) {

        reservationService.cancelReservation(id);

        return "redirect:/reservations";
    }
}