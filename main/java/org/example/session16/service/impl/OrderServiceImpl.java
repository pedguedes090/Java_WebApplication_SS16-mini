package org.example.session16.service.impl;

import org.example.session16.model.dto.CartItem;
import org.example.session16.model.entity.Order;
import org.example.session16.model.entity.OrderDetail;
import org.example.session16.model.entity.Product;
import org.example.session16.repository.OrderDetailRepository;
import org.example.session16.repository.OrderRepository;
import org.example.session16.repository.ProductRepository;
import org.example.session16.service.OrderService;
import org.example.session16.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderDetailRepository orderDetailRepository,
                            ProductRepository productRepository,
                            ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public Order createOrder(
            String customerName,
            String customerEmail,
            String customerPhone,
            String customerAddress,
            String recipientName,
            String recipientPhone,
            String recipientAddress,
            List<CartItem> cartItems
    ) {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // ✅ 1. Kiểm tra tồn kho trước
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        String.format("Sản phẩm '%s' không đủ số lượng. Có sẵn: %d",
                                product.getName(), product.getStock())
                );
            }
        }

        // ✅ 2. Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(
                    item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO
            );
        }

        // ✅ 3. Tạo Order (KHÔNG dùng User)
        Order order = Order.builder()
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .customerAddress(customerAddress)
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .recipientAddress(recipientAddress)
                .totalAmount(totalAmount)
                .status("PENDING")
                .build();

        order = orderRepository.save(order);

        // ✅ 4. Tạo OrderDetail + trừ stock
        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

            // kiểm tra lại lần 2 (tránh race condition)
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        String.format("Sản phẩm '%s' không đủ số lượng. Có sẵn: %d",
                                product.getName(), product.getStock())
                );
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();

            orderDetailRepository.save(orderDetail);

            // trừ tồn kho
            productService.reduceStock(cartItem.getProductId(), cartItem.getQuantity());
        }

        // ✅ 5. Hoàn tất đơn
        order.setStatus("COMPLETED");

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tìm thấy"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}