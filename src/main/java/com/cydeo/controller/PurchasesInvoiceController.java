package com.cydeo.controller;


import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final CompanyService companyService;


    public PurchasesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.companyService = companyService;
    }

    @GetMapping("/create")
    public String createPurchaseInvoice(Model model) {
        model.addAttribute("newPurchaseInvoice", invoiceService.createNewPurchasesInvoice());
        return "/invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String savePurchaseInvoice(@ModelAttribute("newPurchaseInvoice") @Valid InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vendors", clientVendorService.findAllByType(ClientVendorType.VENDOR));
            return "/invoice/purchase-invoice-create";
        }
        InvoiceDto obj1 = invoiceService.save(invoiceDto);
        return "redirect:/purchaseInvoices/update/" + obj1.getId();
    }


    @GetMapping("/update/{id}")
    private String editInvoice(@PathVariable Long id, Model model) {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));

        return "/invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    private String updateInvoice(@ModelAttribute("newPurchaseInvoice") @Valid InvoiceDto invoiceDto, @PathVariable Long invoiceId, Model model) {

        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId));

        InvoiceDto obj1 = invoiceService.update(invoiceDto, invoiceId);
        return "redirect:/purchaseInvoices/update/" + obj1.getId();
    }


    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String saveProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, @PathVariable Long invoiceId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId));
            return "/invoice/purchase-invoice-update";
        }

        invoiceProductService.save(invoiceProductDto, invoiceId);

        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deletePurchasedInvoice(@PathVariable Long id) {
        invoiceService.delete(id);
        return "redirect:/purchaseInvoices/list/";
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{productId}")
    public String deleteInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long productId) {
        invoiceProductService.deleteInvoiceProduct(invoiceId, productId);
        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }


    @GetMapping("/list")
    public String pInvoicesList(Model model) {
        model.addAttribute("invoices", invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.PURCHASE));

        return "/invoice/purchase-invoice-list";
    }

    @GetMapping("/approve/{invoiceId}")
    public String approvePurchaseInvoice(@PathVariable Long invoiceId) {
        invoiceService.approvePurchaseInvoice(invoiceId);
        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("print/{id}")
    public String printPurchasedInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceForPrint(id));
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));

        return "invoice/invoice_print";
    }

    @ModelAttribute
    public void commonModel(Model model){
        model.addAttribute("vendors", clientVendorService.findAllByType(ClientVendorType.VENDOR));
        model.addAttribute("invoices",invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.PURCHASE));
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("company", invoiceService.getCurrentCompany());
        model.addAttribute("title", "Cydeo Accounting-Purchase Invoice");
    }


}
