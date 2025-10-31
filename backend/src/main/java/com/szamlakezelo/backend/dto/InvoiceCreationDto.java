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

    // 2. Kiállítás dátuma (IssueDate) - Ha a backend állítja be, kihagyhatjuk a DTO-ból
    // Mivel a schema nullable=false: Tegyük kötelezővé. Használjunk LocalDate-et a kényelmesebb validáláshoz!
    @NotNull(message = "A kiállítás dátuma kötelező.")
    private LocalDate issueDate; // Backend oldalon kell majd Date-re konvertálni

    // 3. Fizetési határidő (DueDate)
    @NotNull(message = "A fizetési határidő kötelező.")
    @FutureOrPresent(message = "A fizetési határidő nem lehet múltbeli dátum.")
    private LocalDate dueDate;

    // 4. Tétel neve (ItemName)
    @NotBlank(message = "A tétel neve kötelező.")
    @Size(max = 255, message = "A tétel neve túl hosszú.")
    private String itemName;

    // 5. Megjegyzés (Comment)
    @NotBlank(message = "A megjegyzés kötelező.")
    @Size(max = 500, message = "A megjegyzés túl hosszú.")
    private String comment;

    // 6. Ár (Price) - Ez valószínűleg a nettó ár a te rendszeredben, mivel nincs ÁFA mező
    @NotNull(message = "Az ár kötelező.")
    @DecimalMin(value = "0.01", message = "Az árnak pozitívnak kell lennie.")
    private BigDecimal price;
}
