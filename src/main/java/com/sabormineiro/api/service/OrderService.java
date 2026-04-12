package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.OrderRepository;
import com.sabormineiro.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final OrderCalculator orderCalculator;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        log.info("Processing new order request for customer: {}", 
            request.getCustomer() != null ? request.getCustomer().getName() : request.getClientId());

        Client client = customerService.resolveClient(request.getClientId(), request.getCustomer());
        Address address = customerService.resolveAddress(request.getDeliveryAddressId(), client, request.getCustomer());

        Order order = Order.builder()
                .client(client)
                .deliveryAddress(address)
                .guestName(request.getCustomer() != null ? request.getCustomer().getName() : null)
                .paymentMethod(PaymentMethod.fromValue(request.getPaymentMethod()))
                .status(OrderStatus.CREATED)
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));
                    
                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemRequest.getQuantity())
                            .observations(itemRequest.getObservations())
                            .priceAtPurchase(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        order.setItems(items);
        order.setTotal(orderCalculator.calculateTotal(items));

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateStatus(UUID id, String statusValue) {
        log.debug("Updating status for order {} to {}", id, statusValue);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            newStatus = OrderStatus.fromDescription(statusValue);
        }
        
        order.setStatus(newStatus);
        return orderMapper.toDTO(orderRepository.save(order));
    }
}
