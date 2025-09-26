package com.invenza.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="bill_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillItem {

    @ManyToOne
    @JoinColumn(name = "bill_id",nullable = false)
    private Bill bill;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double quotedPrice;

    @Column(nullable = false)
    private double finalPrice;

    @Column(nullable = false)
    private double finalAmount;


}
