package com.invenza.dto;

import com.invenza.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String hsnCode;
    private Double price;
    private String unit;
    private Double gstRate;
    private Double discountValue;

    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getHsnCode(),
                product.getPrice(),
                product.getUnit(),
                product.getGstRate(),
                product.getDiscountValue()
        );
    }
}
