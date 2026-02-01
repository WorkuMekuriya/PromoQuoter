package com.promoquoter.service;

import com.promoquoter.domain.Product;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.dto.ProductRequest;
import com.promoquoter.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getCategory(),
                request.getPrice(),
                request.getStock()
        );
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> createProducts(List<ProductRequest> requests) {
        return requests.stream()
                .map(this::createProduct)
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByIds(List<Long> ids) {
        return productRepository.findByIdIn(ids);
    }
}
