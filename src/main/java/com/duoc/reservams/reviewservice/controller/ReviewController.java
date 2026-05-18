package com.duoc.reservams.reviewservice.controller;

import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controlador REST para manejar reseñas
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // lista todas las reseñas
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    // busca una reseña por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    // lista reseñas de un hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponseDTO>> findByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.findByHotelId(hotelId));
    }

    // lista reseñas de un cliente
    @GetMapping("/client/{clientUserId}")
    public ResponseEntity<List<ReviewResponseDTO>> findByClientUserId(@PathVariable Long clientUserId) {
        return ResponseEntity.ok(reviewService.findByClientUserId(clientUserId));
    }

    // lista reseñas asociadas a una reserva
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<ReviewResponseDTO>> findByReservationId(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reviewService.findByReservationId(reservationId));
    }

    // crea una nueva reseña
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(reviewService.create(request));
    }

    // actualiza una reseña existente
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO request) {

        return ResponseEntity.ok(reviewService.update(id, request));
    }

    // elimina una reseña
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}