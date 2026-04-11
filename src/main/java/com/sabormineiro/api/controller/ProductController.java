package com.sabormineiro.api.controller;

import com.sabormineiro.api.dto.ProductDTO;
import com.sabormineiro.api.entity.Category;
import com.sabormineiro.api.entity.Product;
import com.sabormineiro.api.repository.ProductRepository;
import com.sabormineiro.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        Product product = Product.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .urlImagem(dto.getUrl_imagem())
                .qtdDisp(dto.getQtd_disp())
                .precisaProduzir(dto.getPrecisa_produzir())
                .categoria(Category.fromValue(dto.getCategoria()))
                .build();
        
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.toDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setNome(dto.getNome());
        product.setDescricao(dto.getDescricao());
        product.setPreco(dto.getPreco());
        product.setUrlImagem(dto.getUrl_imagem());
        product.setQtdDisp(dto.getQtd_disp());
        product.setPrecisaProduzir(dto.getPrecisa_produzir());
        product.setCategoria(Category.fromValue(dto.getCategoria()));
        
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
