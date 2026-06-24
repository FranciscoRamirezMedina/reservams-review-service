package com.duoc.reservams.reviewservice.service;

import com.duoc.reservams.reviewservice.client.ReservationClient;
import com.duoc.reservams.reviewservice.dto.ReservationResponseDTO;
import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.model.Review;
import com.duoc.reservams.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// pruebas unitarias para ReviewService
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void findAll_shouldReturnReviews() {
        // Given
        when(reviewRepository.findAll()).thenReturn(List.of(
                buildReview(1L, 1L, 1L, 1L, 5),
                buildReview(2L, 2L, 1L, 2L, 4)
        ));

        // When
        List<ReviewResponseDTO> response = reviewService.findAll();

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(5, response.get(0).getRating());

        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnReview_whenExists() {
        // Given
        Review review = buildReview(1L, 1L, 1L, 1L, 5);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When
        ReviewResponseDTO response = reviewService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getClientUserId());
        assertEquals(1L, response.getHotelId());
        assertEquals(1L, response.getReservationId());
        assertEquals(5, response.getRating());

        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenReviewNotFound() {
        // Given
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.findById(99L)
        );

        // Then
        assertEquals("Reseña no encontrada", exception.getMessage());

        verify(reviewRepository, times(1)).findById(99L);
    }

    @Test
    void findByHotelId_shouldReturnReviews() {
        // Given
        when(reviewRepository.findByHotelId(1L)).thenReturn(List.of(
                buildReview(1L, 1L, 1L, 1L, 5),
                buildReview(2L, 2L, 1L, 2L, 4)
        ));

        // When
        List<ReviewResponseDTO> response = reviewService.findByHotelId(1L);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getHotelId());

        verify(reviewRepository, times(1)).findByHotelId(1L);
    }

    @Test
    void findByClientUserId_shouldReturnReviews() {
        // Given
        when(reviewRepository.findByClientUserId(1L)).thenReturn(List.of(
                buildReview(1L, 1L, 1L, 1L, 5)
        ));

        // When
        List<ReviewResponseDTO> response = reviewService.findByClientUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getClientUserId());

        verify(reviewRepository, times(1)).findByClientUserId(1L);
    }

    @Test
    void findByReservationId_shouldReturnReviews() {
        // Given
        when(reviewRepository.findByReservationId(1L)).thenReturn(List.of(
                buildReview(1L, 1L, 1L, 1L, 5)
        ));

        // When
        List<ReviewResponseDTO> response = reviewService.findByReservationId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getReservationId());

        verify(reviewRepository, times(1)).findByReservationId(1L);
    }

    @Test
    void create_shouldCreateReview_whenReservationIsValid() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();
        ReservationResponseDTO reservation = buildReservationResponse(1L, 1L, 1L, "CONFIRMED");

        when(reviewRepository.existsByReservationId(1L)).thenReturn(false);
        when(reservationClient.findById(1L)).thenReturn(reservation);

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId(1L);
            return review;
        });

        // When
        ReviewResponseDTO response = reviewService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getClientUserId());
        assertEquals(1L, response.getHotelId());
        assertEquals(1L, response.getReservationId());
        assertEquals(5, response.getRating());
        assertEquals("Muy buen hotel", response.getComment());
        assertNotNull(response.getCreatedAt());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void create_shouldThrowException_whenReservationAlreadyHasReview() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();

        when(reviewRepository.existsByReservationId(1L)).thenReturn(true);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(request)
        );

        // Then
        assertEquals("Esta reserva ya tiene una reseña registrada", exception.getMessage());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_shouldThrowException_whenClientDoesNotMatchReservation() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();
        ReservationResponseDTO reservation = buildReservationResponse(1L, 99L, 1L, "CONFIRMED");

        when(reviewRepository.existsByReservationId(1L)).thenReturn(false);
        when(reservationClient.findById(1L)).thenReturn(reservation);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(request)
        );

        // Then
        assertEquals("No se pudo validar la reserva: El cliente no coincide con la reserva",
                exception.getMessage());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_shouldThrowException_whenHotelDoesNotMatchReservation() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();
        ReservationResponseDTO reservation = buildReservationResponse(1L, 1L, 99L, "CONFIRMED");

        when(reviewRepository.existsByReservationId(1L)).thenReturn(false);
        when(reservationClient.findById(1L)).thenReturn(reservation);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(request)
        );

        // Then
        assertEquals("No se pudo validar la reserva: El hotel no coincide con la reserva",
                exception.getMessage());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_shouldThrowException_whenReservationIsNotConfirmed() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();
        ReservationResponseDTO reservation = buildReservationResponse(1L, 1L, 1L, "PENDING");

        when(reviewRepository.existsByReservationId(1L)).thenReturn(false);
        when(reservationClient.findById(1L)).thenReturn(reservation);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(request)
        );

        // Then
        assertEquals("No se pudo validar la reserva: Solo se puede crear reseña para reservas confirmadas",
                exception.getMessage());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_shouldThrowException_whenReservationClientFails() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();

        when(reviewRepository.existsByReservationId(1L)).thenReturn(false);
        when(reservationClient.findById(1L)).thenThrow(new RuntimeException("Reserva no encontrada"));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(request)
        );

        // Then
        assertEquals("No se pudo validar la reserva: Reserva no encontrada", exception.getMessage());

        verify(reviewRepository, times(1)).existsByReservationId(1L);
        verify(reservationClient, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void update_shouldUpdateReview_whenExists() {
        // Given
        Review review = buildReview(1L, 1L, 1L, 1L, 5);
        ReviewRequestDTO request = buildReviewRequest();
        request.setRating(4);
        request.setComment("Buen hotel actualizado");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReviewResponseDTO response = reviewService.update(1L, request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(4, response.getRating());
        assertEquals("Buen hotel actualizado", response.getComment());

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void update_shouldThrowException_whenReviewNotFound() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.update(99L, request)
        );

        // Then
        assertEquals("Reseña no encontrada", exception.getMessage());

        verify(reviewRepository, times(1)).findById(99L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void delete_shouldDeleteReview_whenExists() {
        // Given
        when(reviewRepository.existsById(1L)).thenReturn(true);

        // When
        reviewService.delete(1L);

        // Then
        verify(reviewRepository, times(1)).existsById(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenReviewNotFound() {
        // Given
        when(reviewRepository.existsById(99L)).thenReturn(false);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewService.delete(99L)
        );

        // Then
        assertEquals("Reseña no encontrada", exception.getMessage());

        verify(reviewRepository, times(1)).existsById(99L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    private ReviewRequestDTO buildReviewRequest() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setClientUserId(1L);
        request.setHotelId(1L);
        request.setReservationId(1L);
        request.setRating(5);
        request.setComment("Muy buen hotel");
        return request;
    }

    private Review buildReview(Long id, Long clientUserId, Long hotelId, Long reservationId, Integer rating) {
        Review review = new Review();
        review.setId(id);
        review.setClientUserId(clientUserId);
        review.setHotelId(hotelId);
        review.setReservationId(reservationId);
        review.setRating(rating);
        review.setComment("Muy buen hotel");
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private ReservationResponseDTO buildReservationResponse(
            Long id,
            Long clientUserId,
            Long hotelId,
            String status) {

        ReservationResponseDTO reservation = new ReservationResponseDTO();
        reservation.setId(id);
        reservation.setClientUserId(clientUserId);
        reservation.setHotelId(hotelId);
        reservation.setRoomId(1L);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(2));
        reservation.setTotalAmount(new BigDecimal("90000"));
        reservation.setStatus(status);
        reservation.setCreatedAt(LocalDateTime.now());
        return reservation;
    }
}