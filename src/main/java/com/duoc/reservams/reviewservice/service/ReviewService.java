package com.duoc.reservams.reviewservice.service;

import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.model.Review;
import com.duoc.reservams.reviewservice.repository.ReviewRepository;
import com.duoc.reservams.reviewservice.client.ReservationClient;
import com.duoc.reservams.reviewservice.dto.ReservationResponseDTO;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

// aqui va la lógica de negocio de reseñas
@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    private final ReservationClient reservationClient;

    public ReviewService(ReviewRepository reviewRepository,
                         ReservationClient reservationClient) {
        this.reviewRepository = reviewRepository;
        this.reservationClient = reservationClient;
    }

    public List<ReviewResponseDTO> findAll() {
        logger.info("Listando reseñas");

        return reviewRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO findById(Long id) {
        Review review = findReviewOrThrow(id);

        return toResponseDTO(review);
    }

    public List<ReviewResponseDTO> findByHotelId(Long hotelId) {
        logger.info("Listando reseñas del hotel ID {}", hotelId);

        return reviewRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> findByClientUserId(Long clientUserId) {
        logger.info("Listando reseñas del cliente ID {}", clientUserId);

        return reviewRepository.findByClientUserId(clientUserId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> findByReservationId(Long reservationId) {
        logger.info("Listando reseñas de la reserva ID {}", reservationId);

        return reviewRepository.findByReservationId(reservationId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO create(ReviewRequestDTO request) {
        logger.info("Iniciando creacion de reseña para reserva ID {}, cliente ID {}, hotel ID {}",
                request.getReservationId(),
                request.getClientUserId(),
                request.getHotelId());

        // evitamos que una misma reserva tenga mas de una reseña
        if (reviewRepository.existsByReservationId(request.getReservationId())) {
            logger.warn("No se pudo crear reseña. La reserva ID {} ya tiene una reseña registrada",
                    request.getReservationId());
            throw new RuntimeException("Esta reserva ya tiene una reseña registrada");
        }

        validateReservationForReview(request);

        Review review = new Review();

        review.setClientUserId(request.getClientUserId());
        review.setHotelId(request.getHotelId());
        review.setReservationId(request.getReservationId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        logger.info("Reseña creada correctamente con ID {} para reserva ID {}",
                savedReview.getId(),
                savedReview.getReservationId());

        return toResponseDTO(savedReview);
    }

    public ReviewResponseDTO update(Long id, ReviewRequestDTO request) {
        logger.info("Iniciando actualizacion de reseña ID {}", id);

        Review review = findReviewOrThrow(id);

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        logger.info("Reseña ID {} actualizada correctamente", updatedReview.getId());

        return toResponseDTO(updatedReview);
    }

    public void delete(Long id) {
        logger.info("Iniciando eliminacion de reseña ID {}", id);

        if (!reviewRepository.existsById(id)) {
            logger.warn("Reseña no encontrada con ID {}", id);
            throw new RuntimeException("Reseña no encontrada");
        }

        reviewRepository.deleteById(id);

        logger.info("Reseña ID {} eliminada correctamente", id);
    }

    private void validateReservationForReview(ReviewRequestDTO request) {
        try {
            logger.info("Validando reserva ID {} mediante OpenFeign", request.getReservationId());

            // consultamos reservation-service para verificar que la reserva exista
            ReservationResponseDTO reservation =
                    reservationClient.findById(request.getReservationId());

            // validamos que la reseña coincida con la reserva consultada
            if (!reservation.getClientUserId().equals(request.getClientUserId())) {
                logger.warn("Cliente no coincide para reserva ID {}. Cliente esperado: {}, cliente recibido: {}",
                        request.getReservationId(),
                        reservation.getClientUserId(),
                        request.getClientUserId());

                throw new RuntimeException("El cliente no coincide con la reserva");
            }

            if (!reservation.getHotelId().equals(request.getHotelId())) {
                logger.warn("Hotel no coincide para reserva ID {}. Hotel esperado: {}, hotel recibido: {}",
                        request.getReservationId(),
                        reservation.getHotelId(),
                        request.getHotelId());

                throw new RuntimeException("El hotel no coincide con la reserva");
            }

            // para esta version permitimos reseña solo si la reserva esta confirmada
            if (!reservation.getStatus().equals("CONFIRMED")) {
                logger.warn("No se puede crear reseña para reserva ID {} porque su estado es {}",
                        request.getReservationId(),
                        reservation.getStatus());

                throw new RuntimeException("Solo se puede crear reseña para reservas confirmadas");
            }

            logger.info("Reserva ID {} validada correctamente para crear reseña",
                    request.getReservationId());

        } catch (Exception ex) {
            logger.error("No se pudo validar la reserva ID {}. Detalle: {}",
                    request.getReservationId(),
                    ex.getMessage());

            throw new RuntimeException("No se pudo validar la reserva: " + ex.getMessage());
        }
    }

    private Review findReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Reseña no encontrada con ID {}", id);
                    return new RuntimeException("Reseña no encontrada");
                });
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