package com.sabormineiro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private CustomerDTO customer;
    private List<OrderItemResponseDTO> items;
    private BigDecimal total;
    private String status;
    private String createdAt;
}
