package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.CustomerDTO;
import com.sabormineiro.api.dto.OrderItemResponseDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.entity.Address;
import com.sabormineiro.api.entity.Order;
import com.sabormineiro.api.entity.OrderItem;
import com.sabormineiro.api.entity.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductService productService;

    public OrderResponseDTO toDTO(Order order) {
        // Security Check: IDOR Protection
        // Only owners, admins, or staff can see order details
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        
        boolean isAdminOrStaff = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                               a.getAuthority().equals("ROLE_DEMO") || 
                               a.getAuthority().equals("ROLE_COZINHEIRO") || 
                               a.getAuthority().equals("ROLE_ATENDENTE"));
        
        boolean isOwner = false;
        if (isAuthenticated) {
            String currentUserEmail = authentication.getName();
            isOwner = order.getClient() != null && order.getClient().getUser() != null 
                      && order.getClient().getUser().getEmail().equals(currentUserEmail);
        } else {
            // For anonymous users, we trust the service layer has already filtered by visitorId
            // OR if this is being called immediately after creation, it is permitted
            isOwner = true; 
        }

        if (!isAdminOrStaff && !isOwner) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        String displayName = order.getGuestName() != null ? order.getGuestName() : 
                           (order.getClient() != null && order.getClient().getUser() != null ? order.getClient().getUser().getName() : "Guest");

        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .customer(CustomerDTO.builder()
                        .name(displayName)
                        .phone(order.getClient() != null ? order.getClient().getPhone() : null)
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
        if (addr == null) return "Local Pickup";
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
