package com.szamlakezelo.backend.controller;

import com.szamlakezelo.backend.data.model.Invoice;
import com.szamlakezelo.backend.data.repository.InvoiceRepository;
import com.szamlakezelo.backend.dto.InvoiceCreationDto;
import com.szamlakezelo.backend.dto.InvoiceDto;
import com.szamlakezelo.backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    //1. szamlak listazasa, mindenki latja, aki be van jelentkezve
    // GET /api/invoices/all
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        List<InvoiceDto> invoiceList = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoiceList);
    }

    //2. kivalasztott szamla megtekintese, mindenki latja aki bejelentkezett
    // GET /api/invoices/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Long id) {
        InvoiceDto invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    //3. szamla letrehozasa, csak admin es konvvelo
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOOKKEEPER')")
    public ResponseEntity<Invoice> createInvoice(@RequestBody @Valid InvoiceCreationDto invoice) {
        Invoice newInvoice = invoiceService.createInvoice(invoice);

        return ResponseEntity.ok(newInvoice);
    }


}

