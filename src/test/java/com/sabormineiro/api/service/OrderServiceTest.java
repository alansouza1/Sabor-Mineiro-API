package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.CustomerDTO;
import com.sabormineiro.api.dto.OrderItemRequestDTO;
import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.repository.OrderRepository;
import com.sabormineiro.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .nome("Product 1")
                .preco(BigDecimal.valueOf(10.0))
                .categoria(Category.PRATOS_PRINCIPAIS)
                .build();
    }

    @Test
    void createOrder_ShouldCalculateTotalCorrectly() {
        // Arrange
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("John Doe")
                .phone("123456789")
                .address("123 Street")
                .paymentMethod("pix")
                .build();

        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .customer(customerDTO)
                .items(List.of(itemRequest))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        // Act
        OrderResponseDTO response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(20.0), response.getTotal());
        assertEquals("pending", response.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .customer(Customer.builder()
                        .name("John")
                        .phone("123")
                        .address("Add")
                        .paymentMethod(PaymentMethod.PIX)
                        .build())
                .total(BigDecimal.TEN)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDTO response = orderService.updateStatus(orderId, "preparing");

        // Assert
        assertNotNull(response);
        assertEquals("preparing", response.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
