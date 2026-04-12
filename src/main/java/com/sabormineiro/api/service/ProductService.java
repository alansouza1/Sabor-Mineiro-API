package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.ProductDTO;
import com.sabormineiro.api.entity.Product;
import com.sabormineiro.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .availableQuantity(product.getAvailableQuantity())
                .needsProduction(product.getNeedsProduction())
                .category(product.getCategory().getValue())
                .build();
    }
}
