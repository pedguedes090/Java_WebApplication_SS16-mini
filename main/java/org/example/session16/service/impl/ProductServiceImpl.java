package org.example.session16.service.impl;

import org.example.session16.model.entity.Product;
import org.example.session16.model.dto.ProductSearchDTO;
import org.example.session16.repository.ProductRepository;
import org.example.session16.repository.spec.ProductSpecifications;
import org.example.session16.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(ProductSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(
                searchDTO.getPage() != null ? searchDTO.getPage() : 0,
                searchDTO.getPageSize() != null ? searchDTO.getPageSize() : 10
        );

        Specification<Product> specification = ProductSpecifications.combineSearch(
                searchDTO.getName(),
                searchDTO.getMinPrice(),
                searchDTO.getMaxPrice(),
                searchDTO.getCategoryId()
        );

        return productRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                pageSize != null ? pageSize : 10
        );
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    @Override
    public Product createProduct(Product product) {
        if (product.getSoldQuantity() == null) {
            product.setSoldQuantity(0);
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getTop5BestSelling() {
        return productRepository.findTop5BestSelling();
    }

    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException(
                    String.format("Sản phẩm %s không đủ số lượng. Có sẵn: %d, Yêu cầu: %d",
                            product.getName(), product.getStock(), quantity)
            );
        }

        product.setStock(product.getStock() - quantity);
        product.setSoldQuantity(product.getSoldQuantity() + quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasProductInStock(Long productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStock() >= quantity;
    }
}

