package org.example.session16.controller;

import org.example.session16.model.dto.CartItem;
import org.example.session16.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@SessionAttributes("cartItems")
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Hiển thị trang checkout
    @GetMapping
    public String showCheckoutForm(
            @SessionAttribute(value = "cartItems", required = false) List<CartItem> cartItems,
            Model model) {

        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("error", "Giỏ hàng trống");
            return "redirect:/cart";
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getTotalPrice() != null) {
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout/checkout";
    }

    // Xử lý đặt hàng
    @PostMapping("/submit")
    public String submitCheckout(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("recipientName") String recipientName,
            @RequestParam("recipientPhone") String recipientPhone,
            @RequestParam("recipientAddress") String recipientAddress,
            @SessionAttribute(value = "cartItems", required = false) List<CartItem> cartItems,
            RedirectAttributes redirectAttributes) {

        try {
            if (cartItems == null || cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống");
                return "redirect:/cart";
            }

            // Gọi service tạo đơn (KHÔNG dùng User)
            orderService.createOrder(
                    name,
                    email,
                    phone,
                    address,
                    recipientName,
                    recipientPhone,
                    recipientAddress,
                    cartItems
            );

            // Xóa giỏ hàng sau khi đặt thành công
            cartItems.clear();

            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được tạo thành công!");
            return "redirect:/checkout/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Lỗi tạo đơn hàng: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Trang thành công
    @GetMapping("/success")
    public String showSuccessPage() {
        return "checkout/success";
    }
}