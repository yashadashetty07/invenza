package com.invenza.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "quotation_item")
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Double quantity;

    // Selling price (frontend editable)
    @Column(nullable = false)
    private Double unitPrice;

    // Discount amount (MRP - selling price)
    @Column(nullable = false)
    private Double discountAmount;

    // GST % from product
    @Column(nullable = false)
    private Double gstRate;

    // Total price including GST
    @Column(nullable = false)
    private Double totalPrice;
}
