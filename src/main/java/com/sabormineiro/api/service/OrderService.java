package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.*;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.*;
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
    private final UserRepository userRepository;
    private final CEPRepository cepRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Client client;
        Address address;

        // Smart Resolution Logic for Demo/Quick Checkout
        if (request.getClientId() != null) {
            client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
            
            address = addressRepository.findById(request.getDeliveryAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        } else if (request.getCustomer() != null) {
            // Find system user to link the guest/demo checkout
            User systemUser = userRepository.findByEmail("admin@sabormineiro.com")
                    .orElseGet(() -> userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No users found in system")));

            // FIX: Check if this user already has a client profile to avoid unique constraint violation
            client = clientRepository.findByUser(systemUser).orElseGet(() -> {
                String phone = request.getCustomer().getPhone().replaceAll("\\D", "");
                return clientRepository.save(Client.builder()
                        .celular(phone)
                        .cpf(phone.length() >= 11 ? phone.substring(0, 11) : phone)
                        .user(systemUser)
                        .build());
            });

            // Create temporary address for this order
            CEP defaultCep = cepRepository.findByCep("00000000").orElseGet(() -> 
                cepRepository.save(CEP.builder().cep("00000000").cidade("Belo Horizonte").uf("MG").build())
            );

            address = addressRepository.save(Address.builder()
                    .logradouro(request.getCustomer().getAddress())
                    .numero("S/N")
                    .bairro("Centro")
                    .padrao(false)
                    .client(client)
                    .cep(defaultCep)
                    .build());
        } else {
            throw new IllegalArgumentException("Order must have a client ID or customer data");
        }

        Order order = Order.builder()
                .client(client)
                .deliveryAddress(address)
                .guestName(request.getCustomer() != null ? request.getCustomer().getName() : null)
                .paymentMethod(PaymentMethod.fromValue(request.getPaymentMethod() != null ? request.getPaymentMethod() : "pix"))
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
        String displayName = order.getGuestName() != null ? order.getGuestName() : 
                           (order.getClient().getUser() != null ? order.getClient().getUser().getName() : "Guest");

        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .customer(CustomerDTO.builder()
                        .name(displayName)
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
        return String.format("%s, %s - %s", addr.getLogradouro(), addr.getNumero(), addr.getBairro());
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
