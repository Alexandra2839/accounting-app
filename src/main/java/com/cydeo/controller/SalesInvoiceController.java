package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    @Autowired
    CompanyService companyService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }

    @GetMapping("/create")
    public String createSalesInvoice(Model model) {
        model.addAttribute("newSalesInvoice", invoiceService.createNewSalesInvoice());
        return "/invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String saveSalesInvoice(@ModelAttribute("newSalesInvoice") @Valid InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/invoice/sales-invoice-create";
        }
        InvoiceDto obj1 = invoiceService.saveSalesInvoice(invoiceDto);
        return "redirect:/salesInvoices/update/" + obj1.getId();
    }

    @GetMapping("/update/{id}")
    public String editInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));
        return "/invoice/sales-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updateInvoice(@ModelAttribute("newSalesInvoice") @Valid InvoiceDto invoiceDto, @PathVariable Long invoiceId, Model model) {
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId));
        InvoiceDto obj1 = invoiceService.update(invoiceDto, invoiceId);
        return "redirect:/salesInvoices/update/" + obj1.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String saveProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, @PathVariable Long invoiceId, Model model) {

        boolean stockNotEnough = invoiceProductService.isStockNotEnough(invoiceProductDto);
        if (bindingResult.hasErrors() || stockNotEnough) {
            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId));
            if (invoiceProductDto.getProduct() != null && stockNotEnough) {
                model.addAttribute("error", "Not enough " + invoiceProductDto.getProduct().getName() + " quantity to sell.");
            }
            return "/invoice/sales-invoice-update";
        }
        invoiceProductService.save(invoiceProductDto, invoiceId);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deleteSalesInvoice(@PathVariable Long id) {
        invoiceService.delete(id);
        return "redirect:/salesInvoices/list/";
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{productId}")
    public String deleteInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long productId) {
        invoiceProductService.deleteInvoiceProduct(invoiceId, productId);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/list")
    public String sInvoicesList(Model model) {
        model.addAttribute("invoices", invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.SALES));
        return "/invoice/sales-invoice-list";
    }

    @GetMapping("/approve/{invoiceId}")
    public String approveSalesInvoice(@PathVariable Long invoiceId, Model model) {
        invoiceService.approveSalesInvoice(invoiceId);
        return "redirect:/salesInvoices/list";
    }

    @GetMapping("print/{id}")
    public String printSalesInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));
        model.addAttribute("invoice", invoiceService.getInvoiceForPrint(id));
        return "invoice/invoice_print";
    }

    @ModelAttribute
    public void commonModel(Model model) {
        model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
        model.addAttribute("invoices", invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.SALES));
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("company", invoiceService.getCurrentCompany());
        model.addAttribute("title", "Cydeo Accounting-Sale Invoice");
    }
}

