package com.library.digitallibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.digitallibrary.entity.Reservation;
import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.entity.Book;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByBook(Book book);

    List<Reservation> findByStatus(String status);
}