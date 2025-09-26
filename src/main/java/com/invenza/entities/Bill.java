package com.invenza.entities;

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

    @Column(nullable = false)
    private double grandTotal;

    @Column(nullable = false)
    private double gstTotal;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> items;

}
