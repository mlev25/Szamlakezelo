package com.szamlakezelo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    // Ezt a végpontot használjuk a jogosultság-ellenőrzés tesztelésére!
    @GetMapping("/all")
    // Csak hitelesített felhasználók számára engedélyezett
    @PreAuthorize("hasRole('BOOKKEEPER') or hasRole('ADMIN')")
    public ResponseEntity<String> getAllInvoices() {

        // Normál esetben itt hívnánk az InvoiceService.getAllInvoices() metódust

        return ResponseEntity.ok("Sikeresen hozzáférve a VÉDETT /api/invoices/all végponthoz!");
    }
}

