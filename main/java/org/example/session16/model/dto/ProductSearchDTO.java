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
public class ProductSearchDTO {
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long categoryId;
    private Integer page;
    private Integer pageSize;

    public ProductSearchDTO(Integer page, Integer pageSize) {
        this.page = page != null ? page : 0;
        this.pageSize = pageSize != null ? pageSize : 10;
    }
}

