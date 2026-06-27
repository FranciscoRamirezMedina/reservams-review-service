package com.duoc.reservams.reviewservice.client;

import com.duoc.reservams.reviewservice.dto.ReservationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// cliente Feign para comunicarse con reservation-service
@FeignClient(name = "reservams-reservation-service")
public interface ReservationClient {

    // busca una reserva por ID en reservation-service
    @GetMapping("/api/v1/reservations/{id}")
    ReservationResponseDTO findById(@PathVariable("id") Long id);
}
