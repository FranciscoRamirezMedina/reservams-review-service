package com.duoc.reservams.reviewservice.service;

import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.model.Review;
import com.duoc.reservams.reviewservice.repository.ReviewRepository;
import com.duoc.reservams.reviewservice.client.ReservationClient;
import com.duoc.reservams.reviewservice.dto.ReservationResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// aqui va la lógica de negocio de reseñas
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReservationClient reservationClient;

    public ReviewService(ReviewRepository reviewRepository,
                         ReservationClient reservationClient) {
        this.reviewRepository = reviewRepository;
        this.reservationClient = reservationClient;
    }

    public List<ReviewResponseDTO> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        return toResponseDTO(review);
    }

    public List<ReviewResponseDTO> findByHotelId(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> findByClientUserId(Long clientUserId) {
        return reviewRepository.findByClientUserId(clientUserId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> findByReservationId(Long reservationId) {
        return reviewRepository.findByReservationId(reservationId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO create(ReviewRequestDTO request) {
        // evitamos que una misma reserva tenga mas de una reseña
        if (reviewRepository.existsByReservationId(request.getReservationId())) {
            throw new RuntimeException("Esta reserva ya tiene una reseña registrada");
        }

        try {
            // consultamos reservation-service para verificar que la reserva exista
            ReservationResponseDTO reservation =
                    reservationClient.findById(request.getReservationId());

            // validamos que la reseña coincida con la reserva consultada
            if (!reservation.getClientUserId().equals(request.getClientUserId())) {
                throw new RuntimeException("El cliente no coincide con la reserva");
            }

            if (!reservation.getHotelId().equals(request.getHotelId())) {
                throw new RuntimeException("El hotel no coincide con la reserva");
            }

            // para esta version permitimos reseña solo si la reserva esta confirmada
            if (!reservation.getStatus().equals("CONFIRMED")) {
                throw new RuntimeException("Solo se puede crear reseña para reservas confirmadas");
            }

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo validar la reserva: " + ex.getMessage());
        }

        Review review = new Review();

        review.setClientUserId(request.getClientUserId());
        review.setHotelId(request.getHotelId());
        review.setReservationId(request.getReservationId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return toResponseDTO(savedReview);
    }

    public ReviewResponseDTO update(Long id, ReviewRequestDTO request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        return toResponseDTO(updatedReview);
    }

    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Reseña no encontrada");
        }

        reviewRepository.deleteById(id);
    }

    // convierte la entidad Review a DTO de respuesta
    private ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getClientUserId(),
                review.getHotelId(),
                review.getReservationId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}