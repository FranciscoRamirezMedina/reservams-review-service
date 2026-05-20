package com.duoc.reservams.reviewservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO que recibe datos basicos de una reserva desde reservation-service
@Data
public class ReservationResponseDTO {

    private Long id;
    private Long clientUserId;
    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}