package com.szamlakezelo.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InvoiceDto {
    private Long id;
    private String customerName;
    private Date issueDate;
    private Date dueDate;
    private String itemName;
    private String comment;
    private BigDecimal price;

}
