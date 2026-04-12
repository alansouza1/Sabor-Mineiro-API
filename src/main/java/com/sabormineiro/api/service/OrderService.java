package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.*;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CEPRepository cepRepository;
    private final ProductService productService;
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
                .visitorId(request.getVisitorId())
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
    public List<OrderResponseDTO> findAll(String visitorId) {
        List<Order> orders;
        if (visitorId != null && !visitorId.isEmpty()) {
            orders = orderRepository.findAllByVisitorIdOrderByCreatedAtDesc(visitorId);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        
        return orders.stream()
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
