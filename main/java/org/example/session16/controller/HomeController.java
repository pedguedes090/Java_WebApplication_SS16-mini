package org.example.session16.controller;

import org.example.session16.model.entity.Product;
import org.example.session16.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> recentProducts = productService.getAllProducts();
        if (recentProducts.size() > 10) {
            recentProducts = recentProducts.subList(0, 10);
        }
        model.addAttribute("recentProducts", recentProducts);
        return "index";
    }
}

