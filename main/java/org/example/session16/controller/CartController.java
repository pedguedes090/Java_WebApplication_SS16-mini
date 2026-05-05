package org.example.session16.controller;

import org.example.session16.model.dto.CartItem;
import org.example.session16.model.entity.Product;
import org.example.session16.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cartItems")
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @ModelAttribute("cartItems")
    public List<CartItem> cartItems() {
        return new ArrayList<>();
    }

    @GetMapping
    public String viewCart(@ModelAttribute("cartItems") List<CartItem> cartItems, Model model) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getTotalPrice() != null) {
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @ModelAttribute("cartItems") List<CartItem> cartItems,
            RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0");
            return "redirect:/products";
        }

        // Kiểm tra tồn kho
        if (product.getStock() < quantity) {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Sản phẩm '%s' không đủ số lượng. Có sẵn: %d",
                            product.getName(), product.getStock()));
            return "redirect:/products";
        }

        // Kiểm tra sản phẩm đã có trong giỏ
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (product.getStock() < newQuantity) {
                    redirectAttributes.addFlashAttribute("error",
                            String.format("Số lượng vượt quá tồn kho. Tồn kho còn: %d",
                                    product.getStock()));
                    return "redirect:/products";
                }
                item.updateQuantity(newQuantity);
                found = true;
                break;
            }
        }

        // Thêm sản phẩm mới vào giỏ
        if (!found) {
            CartItem cartItem = CartItem.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .totalPrice(product.getPrice().multiply(new BigDecimal(quantity)))
                    .build();
            cartItems.add(cartItem);
        }

        redirectAttributes.addFlashAttribute("success",
                String.format("Đã thêm '%s' vào giỏ hàng", product.getName()));
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @ModelAttribute("cartItems") List<CartItem> cartItems,
            RedirectAttributes redirectAttributes) {

        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0");
            return "redirect:/cart";
        }

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

        if (product.getStock() < quantity) {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Số lượng vượt quá tồn kho. Tồn kho còn: %d",
                            product.getStock()));
            return "redirect:/cart";
        }

        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.updateQuantity(quantity);
                redirectAttributes.addFlashAttribute("success", "Cập nhật giỏ hàng thành công");
                break;
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("productId") Long productId,
            @ModelAttribute("cartItems") List<CartItem> cartItems,
            RedirectAttributes redirectAttributes) {

        cartItems.removeIf(item -> item.getProductId().equals(productId));
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(
            @ModelAttribute("cartItems")
            List<CartItem> cartItems,
            RedirectAttributes redirectAttributes) {

        if (cartItems != null) {
            cartItems.clear();
            redirectAttributes.addFlashAttribute("success", "Giỏ hàng đã được làm sạch");
        }
        return "redirect:/cart";
    }
}


