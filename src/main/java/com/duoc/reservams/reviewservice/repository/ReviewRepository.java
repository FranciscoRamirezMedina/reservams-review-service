package com.duoc.reservams.reviewservice.repository;

import com.duoc.reservams.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// repository para trabajar con la tabla reviews
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // lista reseñas de un hotel
    List<Review> findByHotelId(Long hotelId);

    // lista reseñas hechas por un cliente
    List<Review> findByClientUserId(Long clientUserId);

    // busca reseñas asociadas a una reserva
    List<Review> findByReservationId(Long reservationId);

    // evita que una misma reserva tenga más de una reseña
    boolean existsByReservationId(Long reservationId);
}