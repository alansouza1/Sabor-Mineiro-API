package com.sabormineiro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private ProductDTO product;
    private Integer quantity;
    private String observations;
    private BigDecimal priceAtPurchase;
}
