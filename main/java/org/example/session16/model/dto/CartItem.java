package org.example.session16.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;

    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        if (this.price != null) {
            this.totalPrice = this.price.multiply(new BigDecimal(newQuantity));
        }
    }

    public void calculateTotal() {
        if (this.price != null && this.quantity != null) {
            this.totalPrice = this.price.multiply(new BigDecimal(this.quantity));
        }
    }
}

