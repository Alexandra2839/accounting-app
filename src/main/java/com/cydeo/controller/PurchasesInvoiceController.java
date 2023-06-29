package com.cydeo.controller;


import com.cydeo.dto.InvoiceDto;

import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    //private final ClientVendorService clientVendorService;
    //private final ProductService productService;

    public PurchasesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/create")
    public String createPurchaseInvoice(Model model){
        model.addAttribute("newPurchaseInvoice", new InvoiceDto());
        //model.addAttribute("vendors", clientVendorsService.findAll());

        return "/invoice/purchase-invoice-create";
    }

    @GetMapping("/list")
    public String pInvoicesList(Model model){
        model.addAttribute("invoices",invoiceService.listOfPurchasedInvoices("P"));
        return"/invoice/purchase-invoice-list";
    }

    @GetMapping("/update/{invoiceId}")
    public String editInvoice(@PathVariable Long invoiceId, Model model){
        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        //model.addAttribute("vendors", clientVendorsService.findAll());
        //model.addAttribute(
        return"/invoice/purchase-invoice-update";
    }
    @PostMapping("/update/{id}")
    public String updateInvoice(@ModelAttribute InvoiceDto invoiceDto){
        invoiceService.update(invoiceDto);
        return "redirect:/purchaseInvoices/list";
    }

}
