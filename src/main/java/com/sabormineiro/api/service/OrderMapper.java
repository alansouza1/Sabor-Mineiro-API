package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.CustomerDTO;
import com.sabormineiro.api.dto.OrderItemResponseDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.entity.Address;
import com.sabormineiro.api.entity.Order;
import com.sabormineiro.api.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductService productService;

    public OrderResponseDTO toDTO(Order order) {
        String displayName = order.getGuestName() != null ? order.getGuestName() : 
                           (order.getClient().getUser() != null ? order.getClient().getUser().getName() : "Guest");

        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .customer(CustomerDTO.builder()
                        .name(displayName)
                        .phone(order.getClient().getPhone())
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
        return String.format("%s, %s - %s", addr.getStreet(), addr.getNumber(), addr.getNeighborhood());
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
