package com.example.resource.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.resource.entity.Reservation;
import com.example.resource.entity.User;
import com.example.resource.repository.ReservationRepository;
import com.example.resource.repository.UserRepository;
import com.example.resource.service.ReservationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        ));

        return reservationRepository.findByUser(user);
    }

    @Override
    public Reservation getReservationById(Long id) {

        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reservation not found with id: " + id
                        ));
    }

    @Override
    public Reservation updateReservation(
            Long id,
            Reservation reservation) {

        Reservation existingReservation =
                getReservationById(id);

        existingReservation.setResource(
                reservation.getResource());

        existingReservation.setStartTime(
                reservation.getStartTime());

        existingReservation.setEndTime(
                reservation.getEndTime());

        existingReservation.setPrice(
                reservation.getPrice());

        existingReservation.setStatus(
                reservation.getStatus());

        return reservationRepository.save(
                existingReservation);
    }

    @Override
    public void deleteReservation(Long id) {

        Reservation existingReservation =
                getReservationById(id);

        reservationRepository.delete(existingReservation);
    }

    @Override
    public List<Reservation> getReservationsByFilter(
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        if (status != null
                && minPrice != null
                && maxPrice != null) {

            return reservationRepository
                    .findByStatusAndPriceGreaterThanEqualAndPriceLessThanEqual(
                            com.example.resource.entity.ReservationStatus
                                    .valueOf(status.toUpperCase()),
                            minPrice,
                            maxPrice
                    );
        }

        if (status != null) {

            return reservationRepository.findByStatus(
                    com.example.resource.entity.ReservationStatus
                            .valueOf(status.toUpperCase())
            );
        }

        if (minPrice != null && maxPrice != null) {

            return reservationRepository.findByPriceGreaterThanEqual(
                            minPrice
                    ).stream()
                    .filter(reservation ->
                            reservation.getPrice()
                                    .compareTo(maxPrice) <= 0)
                    .toList();
        }

        if (minPrice != null) {

            return reservationRepository
                    .findByPriceGreaterThanEqual(minPrice);
        }

        if (maxPrice != null) {

            return reservationRepository
                    .findByPriceLessThanEqual(maxPrice);
        }


        return reservationRepository.findAll();
    }
    @Override
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    @Override
    public Page<Reservation> getReservationsByUser(
            Long userId,
            Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        ));

        return reservationRepository.findByUser(
                user,
                pageable
        );
    }
}