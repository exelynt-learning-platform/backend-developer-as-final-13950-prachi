package com.example.resource.repository;

import com.example.resource.entity.Reservation;
import com.example.resource.entity.ReservationStatus;
import com.example.resource.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    Page<Reservation> findByUser(
            User user,
            Pageable pageable);

    List<Reservation> findByStatus(
            ReservationStatus status);

    List<Reservation> findByPriceGreaterThanEqual(
            BigDecimal minPrice);

    List<Reservation> findByPriceLessThanEqual(
            BigDecimal maxPrice);

    List<Reservation>
    findByStatusAndPriceGreaterThanEqualAndPriceLessThanEqual(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    Page<Reservation> findAll(Pageable pageable);
}