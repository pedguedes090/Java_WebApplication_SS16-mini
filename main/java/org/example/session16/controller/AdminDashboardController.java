package org.example.session16.controller;

import jakarta.servlet.http.HttpSession;
import org.example.session16.model.entity.Product;
import org.example.session16.repository.OrderRepository;
import org.example.session16.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProductService productService;
    private final OrderRepository orderRepository;

    public AdminDashboardController(ProductService productService, OrderRepository orderRepository) {
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Kiểm tra quyền admin
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return "redirect:/auth/login";
        }

        // Lấy tổng doanh thu
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();

        // Lấy 5 sản phẩm bán chạy nhất
        List<Product> bestSellingProducts = productService.getTop5BestSelling();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("bestSellingProducts", bestSellingProducts);

        return "admin/dashboard";
    }
}

