package com.invenza.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bill_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillItem {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double mrpPrice;

    @Column(nullable = false)
    private double discountedPrice; // editable

    @Column(nullable = false)
    private double gstAmount;// calculated


    @Column(nullable = false)
    private double unitFinalPrice;    // per unit final price = discountedPrice + gstAmount

    @Column(nullable = false)
    private double totalFinalPrice;        // total for this item = unitFinalPrice * quantity


    @Column(nullable = false)
    private String hsnCode;

    @Column(nullable = false)
    private String productName;


}
