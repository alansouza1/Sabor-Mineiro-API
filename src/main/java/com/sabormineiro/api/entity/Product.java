package com.sabormineiro.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(name = "url_imagem")
    private String urlImagem;

    @Column(name = "qtd_disp", nullable = false)
    private Integer qtdDisp;

    @Column(name = "precisa_produzir", nullable = false)
    private Boolean precisaProduzir;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category categoria;
}
