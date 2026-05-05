package org.example.session16.controller;

import org.example.session16.model.entity.Category;
import org.example.session16.model.entity.Product;
import org.example.session16.model.dto.ProductSearchDTO;
import org.example.session16.repository.CategoryRepository;
import org.example.session16.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ClientProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ClientProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {

        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoryId(categoryId)
                .page(page)
                .pageSize(12)
                .build();

        Page<Product> productsPage = productService.searchProducts(searchDTO);

        // Lấy danh sách danh mục cho dropdown tìm kiếm
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories", categories);
        model.addAttribute("searchDTO", searchDTO);

        return "products/list";
    }

    @GetMapping("/{id}")
    public String viewProductDetail(@RequestParam("id") Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));
        model.addAttribute("product", product);
        return "products/detail";
    }
}

