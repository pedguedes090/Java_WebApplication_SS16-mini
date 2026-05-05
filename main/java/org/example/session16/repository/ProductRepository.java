package org.example.session16.repository;

import org.example.session16.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE p.soldQuantity > 0 ORDER BY p.soldQuantity DESC LIMIT 5")
    List<Product> findTop5BestSelling();

    long countByCategory_Id(Long categoryId);
}

