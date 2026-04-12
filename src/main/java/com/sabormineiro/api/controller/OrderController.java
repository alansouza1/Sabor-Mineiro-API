package com.sabormineiro.api.controller;

import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.dto.OrderStatusUpdateDTO;
import com.sabormineiro.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEMO')")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        log.info("REST request to create new order");
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEMO') or hasRole('COZINHEIRO') or hasRole('ATENDENTE')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @RequestHeader(value = "X-Visitor-Id", required = false) String visitorId) {
        log.info("REST request to get all orders");
        
        // RBAC Check: If user is DEMO, force filtration by visitorId
        boolean isOnlyDemo = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DEMO")) && 
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isOnlyDemo) {
            log.debug("Demo user detected, filtering by visitorId: {}", visitorId);
            return ResponseEntity.ok(orderService.findAll(visitorId));
        }

        return ResponseEntity.ok(orderService.findAll(null));
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
