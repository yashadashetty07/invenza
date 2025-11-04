    package com.invenza.dto;

    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.invenza.entities.Product;
    import lombok.Data;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class BillItemDTO {
        private ProductDTO product;
        private Long productId;
        private String productName;
        private String hsnCode;
        private double quantity;
        private double mrpPrice;
        private double discountedPrice;
        private double gstAmount;
        private double unitFinalPrice;   // unit final price
        private double totalFinalPrice;   // total for this item
    }
