package com.invenza.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Use only id for equals/hashCode to avoid recursion
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private Date billDate;

    @Column(nullable = false)
    private double totalMRP;

    @Column(nullable = false)
    private double totalDiscounted;

    @Column(nullable = false)
    private double gstTotal;

    @Column(nullable = false)
    private double finalAmount;

    @Column(name = "bill_number", nullable = false, unique = true)
    private String billNumber;

    @Column(name ="customer_address")
    private String customerAddress;

    @Column(name = "customer_gstin")
    private String customerGSTIN;

    @JsonManagedReference
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> items = new ArrayList<>();
}
