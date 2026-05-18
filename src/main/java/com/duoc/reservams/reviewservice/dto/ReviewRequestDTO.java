package com.duoc.reservams.reviewservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO para crear una reseña
@Data
public class ReviewRequestDTO {

    @NotNull(message = "El clientUserId es obligatorio")
    private Long clientUserId;

    @NotNull(message = "El hotelId es obligatorio")
    private Long hotelId;

    @NotNull(message = "El reservationId es obligatorio")
    private Long reservationId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    private String comment;
}