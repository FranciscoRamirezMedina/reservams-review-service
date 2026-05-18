package com.duoc.reservams.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// DTO para responder datos de una reseña
@Data
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long id;
    private Long clientUserId;
    private Long hotelId;
    private Long reservationId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}