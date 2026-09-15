package com.library.digitallibrary.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.library.digitallibrary.entity.Book;
import com.library.digitallibrary.entity.Reservation;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Create reservation
    public Reservation reserveBook(User user, Book book) {

        // Reservation tabhi hogi jab book currently unavailable ho
        if (book.getAvailableQuantity() > 0) {
            throw new RuntimeException("Book is currently available. You can issue it directly.");
        }

        // Same user ki same book ki active reservation already hai ya nahi
        List<Reservation> userReservations =
                reservationRepository.findByUser(user);

        for (Reservation reservation : userReservations) {
            if (reservation.getBook().getId().equals(book.getId())
                    && "ACTIVE".equals(reservation.getStatus())) {
                throw new RuntimeException("You have already reserved this book.");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus("ACTIVE");

        return reservationRepository.save(reservation);
    }

    // Get user's reservations
    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUser(user);
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Cancel reservation
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reservation not found"));

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }
}