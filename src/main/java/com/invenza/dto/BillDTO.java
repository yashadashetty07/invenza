package com.invenza.dto;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class BillDTO {
    private Long id;
    private String customerName;
    private Date billDate;
    private double totalMRP;
    private double totalDiscounted;
    private double gstTotal;
    private double finalAmount;
    private String billNumber;
    private String customerAddress;
    private String customerGSTIN;
    private List<BillItemDTO> items;
}
