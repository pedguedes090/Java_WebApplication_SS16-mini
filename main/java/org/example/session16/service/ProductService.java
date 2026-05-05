package org.example.session16.service;

import org.example.session16.model.entity.Product;
import org.example.session16.model.dto.ProductSearchDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Page<Product> searchProducts(ProductSearchDTO searchDTO);

    Page<Product> getProductsByCategory(Long categoryId, Integer page, Integer pageSize);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    void deleteProduct(Long id);

    List<Product> getTop5BestSelling();

    void reduceStock(Long productId, Integer quantity);

    boolean hasProductInStock(Long productId, Integer quantity);
}

