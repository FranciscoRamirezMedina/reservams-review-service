package com.duoc.reservams.reviewservice.controller;

import com.duoc.reservams.reviewservice.dto.ReviewRequestDTO;
import com.duoc.reservams.reviewservice.dto.ReviewResponseDTO;
import com.duoc.reservams.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// pruebas unitarias para ReviewController
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void findAll_shouldReturnReviews() {
        // Given
        when(reviewService.findAll()).thenReturn(List.of(
                buildReviewResponse(1L, 1L, 1L, 1L, 5),
                buildReviewResponse(2L, 2L, 1L, 2L, 4)
        ));

        // When
        ResponseEntity<List<ReviewResponseDTO>> response = reviewController.findAll();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(reviewService, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnReview() {
        // Given
        when(reviewService.findById(1L)).thenReturn(
                buildReviewResponse(1L, 1L, 1L, 1L, 5)
        );

        // When
        ResponseEntity<ReviewResponseDTO> response = reviewController.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(5, response.getBody().getRating());

        verify(reviewService, times(1)).findById(1L);
    }

    @Test
    void findByHotelId_shouldReturnReviews() {
        // Given
        when(reviewService.findByHotelId(1L)).thenReturn(List.of(
                buildReviewResponse(1L, 1L, 1L, 1L, 5),
                buildReviewResponse(2L, 2L, 1L, 2L, 4)
        ));

        // When
        ResponseEntity<List<ReviewResponseDTO>> response = reviewController.findByHotelId(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getHotelId());

        verify(reviewService, times(1)).findByHotelId(1L);
    }

    @Test
    void findByClientUserId_shouldReturnReviews() {
        // Given
        when(reviewService.findByClientUserId(1L)).thenReturn(List.of(
                buildReviewResponse(1L, 1L, 1L, 1L, 5)
        ));

        // When
        ResponseEntity<List<ReviewResponseDTO>> response = reviewController.findByClientUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getClientUserId());

        verify(reviewService, times(1)).findByClientUserId(1L);
    }

    @Test
    void findByReservationId_shouldReturnReviews() {
        // Given
        when(reviewService.findByReservationId(1L)).thenReturn(List.of(
                buildReviewResponse(1L, 1L, 1L, 1L, 5)
        ));

        // When
        ResponseEntity<List<ReviewResponseDTO>> response = reviewController.findByReservationId(1L);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getReservationId());

        verify(reviewService, times(1)).findByReservationId(1L);
    }

    @Test
    void create_shouldReturnCreatedReview() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();

        when(reviewService.create(request)).thenReturn(
                buildReviewResponse(1L, 1L, 1L, 1L, 5)
        );

        // When
        ResponseEntity<ReviewResponseDTO> response = reviewController.create(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(5, response.getBody().getRating());
        assertEquals("Muy buen hotel", response.getBody().getComment());

        verify(reviewService, times(1)).create(request);
    }

    @Test
    void update_shouldReturnUpdatedReview() {
        // Given
        ReviewRequestDTO request = buildReviewRequest();
        request.setRating(4);
        request.setComment("Buen hotel actualizado");

        when(reviewService.update(1L, request)).thenReturn(
                buildReviewResponse(1L, 1L, 1L, 1L, 4)
        );

        // When
        ResponseEntity<ReviewResponseDTO> response = reviewController.update(1L, request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(4, response.getBody().getRating());

        verify(reviewService, times(1)).update(1L, request);
    }

    @Test
    void delete_shouldReturnNoContent() {
        // Given
        doNothing().when(reviewService).delete(1L);

        // When
        ResponseEntity<Void> response = reviewController.delete(1L);

        // Then
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(reviewService, times(1)).delete(1L);
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

    private ReviewResponseDTO buildReviewResponse(
            Long id,
            Long clientUserId,
            Long hotelId,
            Long reservationId,
            Integer rating) {

        return new ReviewResponseDTO(
                id,
                clientUserId,
                hotelId,
                reservationId,
                rating,
                "Muy buen hotel",
                LocalDateTime.now()
        );
    }
}