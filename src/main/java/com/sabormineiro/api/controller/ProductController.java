package com.sabormineiro.api.controller;

import com.sabormineiro.api.dto.ProductDTO;
import com.sabormineiro.api.entity.Category;
import com.sabormineiro.api.entity.Product;
import com.sabormineiro.api.repository.ProductRepository;
import com.sabormineiro.api.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Catalog management and menu listing")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("REST request to get all products");
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO dto) {
        log.info("REST request to create product: {}", dto.getName());
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .availableQuantity(dto.getAvailableQuantity())
                .needsProduction(dto.getNeedsProduction())
                .category(Category.fromValue(dto.getCategory()))
                .build();
        
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.toDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        log.info("REST request to update product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailableQuantity(dto.getAvailableQuantity());
        product.setNeedsProduction(dto.getNeedsProduction());
        product.setCategory(Category.fromValue(dto.getCategory()));
        
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("REST request to delete product ID: {}", id);
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
