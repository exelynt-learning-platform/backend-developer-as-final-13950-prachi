package com.example.resource.service;

import com.example.resource.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);

    List<Reservation> getAllReservations();

    Page<Reservation> getAllReservations(Pageable pageable);

    List<Reservation> getReservationsByUser(Long userId);

    Page<Reservation> getReservationsByUser(
            Long userId,
            Pageable pageable);

    Reservation getReservationById(Long id);

    Reservation updateReservation(
            Long id,
            Reservation reservation);

    void deleteReservation(Long id);

    List<Reservation> getReservationsByFilter(
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice);
}