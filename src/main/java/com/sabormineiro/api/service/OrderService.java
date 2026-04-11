package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.*;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.AddressRepository;
import com.sabormineiro.api.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        Address address = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("Address does not belong to the client");
        }

        Order order = Order.builder()
                .client(client)
                .deliveryAddress(address)
                .paymentMethod(PaymentMethod.fromValue(request.getPaymentMethod()))
                .status(OrderStatus.CRIADO)
                .total(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));
                    
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        // Try to find status by name (enum) or description
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            newStatus = OrderStatus.fromDescription(statusValue);
        }
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return toDTO(updatedOrder);
    }

    private OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .customer(CustomerDTO.builder()
                        .name(order.getClient().getUser().getName())
                        .phone(order.getClient().getCelular())
                        .address(formatAddress(order.getDeliveryAddress()))
                        .paymentMethod(order.getPaymentMethod().getValue())
                        .build())
                .items(order.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()))
                .total(order.getTotal())
                .status(order.getStatus().getDescription())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .build();
    }

    private String formatAddress(Address addr) {
        return String.format("%s, %s - %s, %s - %s", 
            addr.getLogradouro(), addr.getNumero(), addr.getBairro(), addr.getCep().getCidade(), addr.getCep().getUf());
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
