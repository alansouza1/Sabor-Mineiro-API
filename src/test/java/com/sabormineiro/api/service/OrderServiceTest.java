package com.sabormineiro.api.service;

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
    private CustomerService customerService;

    @Mock
    private OrderCalculator orderCalculator;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;
    private Client testClient;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(10.0))
                .category(Category.PRATOS_PRINCIPAIS)
                .build();

        User user = User.builder().id(1L).name("John Doe").email("john@example.com").build();
        testClient = Client.builder().id(1L).user(user).phone("123456789").cpf("12345678901").build();
        
        CEP cep = CEP.builder().cep("12345678").city("BH").state("MG").build();
        testAddress = Address.builder().id(1L).street("Rua A").number("1").neighborhood("Centro").cep(cep).client(testClient).build();
    }

    @Test
    void createOrder_ShouldCalculateTotalAndSave() {
        // Arrange
        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .clientId(1L)
                .deliveryAddressId(1L)
                .paymentMethod("pix")
                .items(List.of(itemRequest))
                .build();

        when(customerService.resolveClient(any(), any())).thenReturn(testClient);
        when(customerService.resolveAddress(any(), any(), any())).thenReturn(testAddress);
        when(productRepository.findAllById(any())).thenReturn(List.of(testProduct));
        
        Order savedOrder = Order.builder().id(UUID.randomUUID()).build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderCalculator.calculateTotal(any())).thenReturn(BigDecimal.valueOf(20.0));
        when(orderMapper.toDTO(any())).thenReturn(OrderResponseDTO.builder().total(BigDecimal.valueOf(20.0)).status("Created").build());

        // Act
        OrderResponseDTO response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(response.getTotal()));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderCalculator, times(1)).calculateTotal(any());
    }

    @Test
    void updateStatus_ShouldUpdateSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDTO(any())).thenReturn(OrderResponseDTO.builder().status("In production").build());

        // Act
        OrderResponseDTO response = orderService.updateStatus(orderId, "in_production");

        // Assert
        assertNotNull(response);
        assertEquals("In production", response.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
