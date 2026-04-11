package com.sabormineiro.api.controller;

import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.dto.OrderStatusUpdateDTO;
import com.sabormineiro.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COZINHEIRO') or hasRole('ATENDENTE')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COZINHEIRO') or hasRole('ATENDENTE')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody OrderStatusUpdateDTO statusUpdate) {
        return ResponseEntity.ok(orderService.updateStatus(id, statusUpdate.getStatus()));
    }
}
