package com.duoc.reservams.reviewservice.service;

import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.model.Review;
import com.duoc.reservams.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// aqui va la lógica de negocio de reseñas
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
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
        // evita que una misma reserva tenga mas de una reseña
        if (reviewRepository.existsByReservationId(request.getReservationId())) {
            throw new RuntimeException("Esta reserva ya tiene una reseña registrada");
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