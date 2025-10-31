package com.szamlakezelo.backend.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date", nullable = false)
    private Date issueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
