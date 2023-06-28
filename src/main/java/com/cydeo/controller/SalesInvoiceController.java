package com.cydeo.controller;

import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;

    public SalesInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/list")
    public String sInvoicesList(Model model){
        model.addAttribute("invoices",invoiceService.listOfPurchasedInvoices("S"));
        return"/invoice/sales-invoice-list";
    }
}

