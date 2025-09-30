package com.invenza.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "bill_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Use only id for equals/hashCode to avoid recursion
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double mrpPrice;

    @Column(nullable = false)
    private double discountedPrice;

    @Column(nullable = false)
    private double gstAmount;

    @Column(nullable = false)
    private double unitFinalPrice;

    @Column(nullable = false)
    private double totalFinalPrice;

    @Column(nullable = false)
    private String hsnCode;

    @Column(nullable = false)
    private String productName;

}
