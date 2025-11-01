package com.szamlakezelo.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceCreationDto {
    @NotBlank(message = "A partner neve kötelező.")
    @Size(max = 100, message = "A partner neve legfeljebb 100 karakter lehet.")
    private String customerName;

    @NotNull(message = "A kiállítás dátuma kötelező.")
    private LocalDate issueDate;

    @NotNull(message = "A fizetési határidő kötelező.")
    @FutureOrPresent(message = "A fizetési határidő nem lehet múltbeli dátum.")
    private LocalDate dueDate;

    @NotBlank(message = "A tétel neve kötelező.")
    @Size(max = 255, message = "A tétel neve túl hosszú.")
    private String itemName;

    @NotBlank(message = "A megjegyzés kötelező.")
    @Size(max = 500, message = "A megjegyzés túl hosszú.")
    private String comment;

    @NotNull(message = "Az ár kötelező.")
    @DecimalMin(value = "0.01", message = "Az árnak pozitívnak kell lennie.")
    private BigDecimal price;
}
