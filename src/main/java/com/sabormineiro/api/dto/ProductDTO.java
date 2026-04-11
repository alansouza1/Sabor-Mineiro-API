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
public class ProductDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String url_imagem;
    private Integer qtd_disp;
    private Boolean precisa_produzir;
    private String categoria;
}
