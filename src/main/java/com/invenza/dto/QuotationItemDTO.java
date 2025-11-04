package com.invenza.dto;

import lombok.Data;

@Data
public class QuotationItemDTO {
    private Long productId;
    private String productName;
    private String hsnCode;
    private Double quantity;
    private Double mrpPrice;          // original product MRP
    private Double discountedPrice;   // editable selling price
    private Double totalPrice;        // discountedPrice * quantity + GST
    private Double gstRate;           // from Product
    private Double discount;          // computed %: (MRP - discountedPrice)/MRP*100
}
