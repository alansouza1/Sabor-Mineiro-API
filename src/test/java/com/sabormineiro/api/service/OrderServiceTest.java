package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.OrderItemRequestDTO;
import com.sabormineiro.api.dto.OrderRequestDTO;
import com.sabormineiro.api.dto.OrderResponseDTO;
import com.sabormineiro.api.entity.*;
import com.sabormineiro.api.repository.AddressRepository;
import com.sabormineiro.api.repository.ClientRepository;
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
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;
    private Client testClient;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .nome("Product 1")
                .preco(BigDecimal.valueOf(10.0))
                .categoria(Category.PRATOS_PRINCIPAIS)
                .build();

        User user = User.builder().id(1L).name("John Doe").email("john@example.com").build();
        testClient = Client.builder().id(1L).user(user).celular("123456789").cpf("12345678901").build();
        
        CEP cep = CEP.builder().cep("12345678").cidade("BH").uf("MG").build();
        testAddress = Address.builder().id(1L).logradouro("Rua A").numero("1").bairro("Centro").cep(cep).client(testClient).build();
    }

    @Test
    void createOrder_ShouldCalculateTotalCorrectly() {
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

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = (Order) invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        // Act
        OrderResponseDTO response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(response.getTotal()));
        assertEquals(OrderStatus.CRIADO.getDescription(), response.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.CRIADO)
                .client(testClient)
                .deliveryAddress(testAddress)
                .paymentMethod(PaymentMethod.PIX)
                .total(BigDecimal.TEN)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDTO response = orderService.updateStatus(orderId, "em_producao");

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.EM_PRODUCAO.getDescription(), response.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
