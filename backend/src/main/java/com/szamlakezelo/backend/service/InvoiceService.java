package com.szamlakezelo.backend.service;

import com.szamlakezelo.backend.data.model.Invoice;
import com.szamlakezelo.backend.data.model.User;
import com.szamlakezelo.backend.data.repository.InvoiceRepository;
import com.szamlakezelo.backend.data.repository.UserRepository;
import com.szamlakezelo.backend.dto.InvoiceCreationDto;
import com.szamlakezelo.backend.dto.InvoiceDto;
import com.szamlakezelo.backend.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;


    public InvoiceService(InvoiceRepository invoiceRepository, UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            return ((UserPrincipal) principal).getId();
        }
        throw new IllegalStateException("Nincs bejelentkezett felhasznalo!");
    }

    //1. minden szamla listazasa
    public List<InvoiceDto> getAllInvoices(){
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToInvoiceDto)
                .toList();
    }

    //2. szamla megtekintese
    public InvoiceDto getInvoiceById(Long id){
        Invoice invoice =  invoiceRepository.findById(id).orElseThrow(() -> new IllegalStateException("Nincs ilyen szamla a megadott ID-val: " + id));
        return mapToInvoiceDto(invoice);
    }

    //3. szamla letrehozasa
    public Invoice createInvoice(InvoiceCreationDto invoiceDto){
        Long creatorId = getCurrentUserId();

        User creator = userRepository.findById(creatorId).orElseThrow(() -> new IllegalStateException("Nincs ilyen felhasznalo: " + creatorId));
        Invoice newInvoice = new Invoice();

        newInvoice.setCustomerName(invoiceDto.getCustomerName());
        newInvoice.setItemName(invoiceDto.getItemName());
        newInvoice.setComment(invoiceDto.getComment());
        newInvoice.setPrice(invoiceDto.getPrice());

        newInvoice.setIssueDate(Date.valueOf(invoiceDto.getIssueDate()));
        newInvoice.setDueDate(Date.valueOf(invoiceDto.getDueDate()));

        return invoiceRepository.save(newInvoice);
    }

    //sajat mapper, igen lehetne mapstruck vagy modelmapper is, de most egyszeruseg kedveert
    private InvoiceDto mapToInvoiceDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setItemName(invoice.getItemName());
        dto.setComment(invoice.getComment());
        dto.setPrice(invoice.getPrice());
        return dto;
    }
}
