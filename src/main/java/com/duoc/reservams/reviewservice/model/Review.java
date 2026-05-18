package com.duoc.reservams.reviewservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// esta clase representa una reseña hecha por un cliente
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    // ID principal de la reseña
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID logico del cliente que viene desde user-service
    @Column(name = "client_user_id", nullable = false)
    private Long clientUserId;

    // ID logico del hotel que viene desde hotel-service
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    // ID logico de la reserva que viene desde reservation-service
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    // Calificacion del hotel, ej de 1 a 5
    @Column(nullable = false)
    private Integer rating;

    // comentario escrito por el cliente
    @Column(length = 255)
    private String comment;

    // fecha en que se creo la reseña
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}