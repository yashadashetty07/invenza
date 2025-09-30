    package com.invenza.dto;

    import com.invenza.entities.Product;
    import lombok.Data;

    @Data
    public class BillItemDTO {
        private Product product;
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

