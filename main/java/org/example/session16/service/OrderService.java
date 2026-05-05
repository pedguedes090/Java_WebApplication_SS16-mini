package org.example.session16.service;

import org.example.session16.model.dto.CartItem;
import org.example.session16.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(
            String customerName,
            String customerEmail,
            String customerPhone,
            String customerAddress,
            String recipientName,
            String recipientPhone,
            String recipientAddress,
            List<CartItem> cartItems
    );

    Optional<Order> getOrderById(Long id);

    List<Order> getAllOrders();

    Order updateOrderStatus(Long orderId, String status);
}