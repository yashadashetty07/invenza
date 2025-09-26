package com.invenza.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "hsn_code", nullable = false,unique = true)
    private String hsnCode;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double gstPercentage;

    @Column(nullable = false)
    private double quantity;        //stock remaining
}
