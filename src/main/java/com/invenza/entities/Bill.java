package com.invenza.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "bills")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private Date billDate;

    // ✅ Totals before GST
    @Column(nullable = false)
    private double totalMRP;

    @Column(nullable = false)
    private double totalDiscounted;

    // ✅ GST applied at bill level
    @Column(nullable = false)
    private double gstTotal;

    // ✅ Final amount = totalDiscounted + gstTotal
    @Column(nullable = false)
    private double finalAmount;

    @JsonManagedReference
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> items;
}
