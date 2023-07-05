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
        model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
        model.addAttribute("products", productService.listAllProducts());

        return "/invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String saveSalesInvoice(@ModelAttribute("newSalesInvoice") @Valid InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
            return "/invoice/sales-invoice-create";
        }
        model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
        InvoiceDto obj1 = invoiceService.saveSalesInvoice(invoiceDto);

        return "redirect:/salesInvoices/update/" + obj1.getId();
    }

    @GetMapping("/update/{id}")
    private String editInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));

        return "/invoice/sales-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    private String updateInvoice(@ModelAttribute("newSalesInvoice") @Valid InvoiceDto invoiceDto, @PathVariable Long invoiceId, Model model) {

        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId));
        InvoiceDto obj1 = invoiceService.update(invoiceDto, invoiceId);
        return "redirect:/salesInvoices/update/" + obj1.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String saveProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, @PathVariable Long invoiceId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findById(invoiceId));//invoice 14
            model.addAttribute("clients", clientVendorService.findAllByType(ClientVendorType.CLIENT));
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(invoiceId)); //all products from invoice 14
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
        invoiceService.approve(invoiceId);
        return "redirect:/salesInvoices/list";
    }

    @GetMapping("print/{id}")
    public String printSalesInvoice(@PathVariable Long id, Model model) {

        model.addAttribute("company", companyService.getCompanyDtoByLoggedInUser());
        model.addAttribute("client", clientVendorService.findById(invoiceService.findById(id).getId()));
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));
        model.addAttribute("invoice", invoiceService.calculateInvoiceSummary(invoiceService.findById(id)));

        return "invoice/invoice_print";
    }
}

