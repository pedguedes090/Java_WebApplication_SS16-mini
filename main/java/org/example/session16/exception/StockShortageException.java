package org.example.session16.exception;

public class StockShortageException extends RuntimeException {
    private String productName;
    private Integer availableStock;
    private Integer requestedQuantity;

    public StockShortageException(String productName, Integer availableStock, Integer requestedQuantity) {
        super(String.format("Sản phẩm '%s' không đủ số lượng. Có sẵn: %d, Yêu cầu: %d", 
                productName, availableStock, requestedQuantity));
        this.productName = productName;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public StockShortageException(String message) {
        super(message);
    }

    public String getProductName() {
        return productName;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
}

