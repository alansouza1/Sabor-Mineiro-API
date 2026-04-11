package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.*;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.OrderRepository;
import com.sabormineiro.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Order order = Order.builder()
                .customer(mapCustomer(request.getCustomer()))
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));
                    
                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemRequest.getQuantity())
                            .observations(itemRequest.getObservations())
                            .priceAtPurchase(product.getPreco())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        return toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateStatus(UUID id, String statusValue) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        order.setStatus(OrderStatus.fromValue(statusValue));
        Order updatedOrder = orderRepository.save(order);
        return toDTO(updatedOrder);
    }

    private Customer mapCustomer(CustomerDTO dto) {
        return Customer.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .paymentMethod(PaymentMethod.fromValue(dto.getPaymentMethod()))
                .build();
    }

    private OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .customer(CustomerDTO.builder()
                        .name(order.getCustomer().getName())
                        .phone(order.getCustomer().getPhone())
                        .address(order.getCustomer().getAddress())
                        .paymentMethod(order.getCustomer().getPaymentMethod().getValue())
                        .build())
                .items(order.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()))
                .total(order.getTotal())
                .status(order.getStatus().getValue())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .build();
    }

    private OrderItemResponseDTO toItemDTO(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .product(productService.toDTO(item.getProduct()))
                .quantity(item.getQuantity())
                .observations(item.getObservations())
                .priceAtPurchase(item.getPriceAtPurchase())
                .build();
    }
}
