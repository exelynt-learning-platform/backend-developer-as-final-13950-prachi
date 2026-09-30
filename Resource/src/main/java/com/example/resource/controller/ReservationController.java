package com.example.resource.controller;

import com.example.resource.dto.ReservationRequest;
import com.example.resource.dto.ReservationResponse;
import com.example.resource.entity.Reservation;
import com.example.resource.entity.ReservationStatus;
import com.example.resource.entity.Resource;
import com.example.resource.entity.User;
import com.example.resource.repository.ResourceRepository;
import com.example.resource.repository.UserRepository;
import com.example.resource.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationController(
            ReservationService reservationService,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationService = reservationService;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new RuntimeException("Resource not found"));

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());

        if (request.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        } else {
            reservation.setStatus(request.getStatus());
        }

        Reservation savedReservation =
                reservationService.createReservation(reservation);

        ReservationResponse response =
                mapToResponse(savedReservation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Pageable pageable;

        if (sort != null && !sort.isBlank()) {

            String[] sortParts = sort.split(",");

            String field = sortParts[0];

            Sort.Direction direction =
                    sortParts.length > 1
                            && sortParts[1].equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(direction, field)
            );

        } else {

            pageable = PageRequest.of(page, size);
        }

        Page<Reservation> reservations;

        if (user.getRole().name().equals("ADMIN")) {

            List<Reservation> filteredReservations =
                    reservationService.getReservationsByFilter(
                            status,
                            minPrice,
                            maxPrice
                    );

            if (sort != null && !sort.isBlank()) {

                String[] sortParts = sort.split(",");

                String field = sortParts[0];

                boolean descending =
                        sortParts.length > 1
                                && sortParts[1].equalsIgnoreCase("desc");

                filteredReservations.sort((r1, r2) -> {

                    int result = 0;

                    if (field.equalsIgnoreCase("price")) {

                        result = r1.getPrice()
                                .compareTo(r2.getPrice());

                    } else if (field.equalsIgnoreCase("startTime")) {

                        result = r1.getStartTime()
                                .compareTo(r2.getStartTime());

                    } else if (field.equalsIgnoreCase("endTime")) {

                        result = r1.getEndTime()
                                .compareTo(r2.getEndTime());
                    }

                    return descending ? -result : result;
                });
            }

            int start = Math.min(
                    page * size,
                    filteredReservations.size()
            );

            int end = Math.min(
                    start + size,
                    filteredReservations.size()
            );

            List<Reservation> pageContent =
                    filteredReservations.subList(start, end);

            reservations = new PageImpl<>(
                    pageContent,
                    pageable,
                    filteredReservations.size()
            );

        } else {

            reservations =
                    reservationService.getReservationsByUser(
                            user.getId(),
                            pageable
                    );
        }

        Page<ReservationResponse> response =
                reservations.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationById(id);

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")
                && !reservation.getUser().getId().equals(user.getId())) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                mapToResponse(reservation)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        Reservation existingReservation =
                reservationService.getReservationById(id);

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")
                && !existingReservation.getUser().getId().equals(user.getId())) {

            return ResponseEntity.status(403).build();
        }

        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new RuntimeException("Resource not found"));

        Reservation reservation = new Reservation();

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());

        reservation.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : existingReservation.getStatus()
        );

        Reservation updatedReservation =
                reservationService.updateReservation(
                        id,
                        reservation
                );

        return ResponseEntity.ok(
                mapToResponse(updatedReservation)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            Authentication authentication) {

        Reservation reservation =
                reservationService.getReservationById(id);

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")
                && !reservation.getUser().getId().equals(user.getId())) {

            return ResponseEntity.status(403).build();
        }

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getUser().getUsername(),
                reservation.getResource().getId(),
                reservation.getResource().getName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getPrice(),
                reservation.getStatus()
        );
    }
}