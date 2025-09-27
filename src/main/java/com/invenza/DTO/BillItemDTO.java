package com.invenza.DTO;

import lombok.Data;

@Data
public class BillItemDTO {
    private Long id;
    private String productName;
    private String hsnCode;
    private double quantity;
    private double mrpPrice;
    private double discountedPrice;
    private double gstAmount;
    private double finalPrice;
}
