package com.sabormineiro.api.controller;

import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.dto.OrderStatusUpdateDTO;
import com.sabormineiro.api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and lifecycle management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        log.info("REST request to create new order");
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            Authentication authentication,
            @RequestHeader(value = "X-Visitor-Id", required = false) String visitorId) {
        log.info("REST request to get all orders");
        
        return ResponseEntity.ok(orderService.findAll(authentication, visitorId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COZINHEIRO') or hasRole('ATENDENTE')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdate) {
        log.info("REST request to update status for order: {}", id);
        return ResponseEntity.ok(orderService.updateStatus(id, statusUpdate.getStatus()));
    }
}
