package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.impl.InvoiceServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }
    @GetMapping("/create")
    public String createSalesInvoice(Model model){


        model.addAttribute("newSalesInvoice",invoiceService.createNewSalesInvoice());
        model.addAttribute("clients", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        return "/invoice/sales-invoice-create";
    }
    @PostMapping("/create")
    public String saveSalesInvoice(@ModelAttribute("newSalesInvoice")InvoiceDto invoiceDto, Model model){
        model.addAttribute("clients", clientVendorService.findAll());
        InvoiceDto obj1 = invoiceService.saveSalesInvoice(invoiceDto);

        return "redirect:/salesInvoices/update/"+obj1.getId();
    }
    @GetMapping("/update/{id}")
    private String editInvoice(@PathVariable Long id, Model model){


        model.addAttribute("invoice",invoiceService.findById(id));//invoice 14

        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());//invoice product taking PathVariable (14)

        model.addAttribute("clients", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id)); //all products from invoice 14

        return "/invoice/sales-invoice-update";
    }
    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String saveProduct(@ModelAttribute("newInvoiceProduct")InvoiceProductDto invoiceProductDto, @PathVariable Long invoiceId,Model model){


        invoiceProductService.save(invoiceProductDto,invoiceId);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }
    @GetMapping("/delete/{id}")
    public String deleteSalesInvoice(@PathVariable Long id){
        invoiceService.delete(id);
        return "redirect:/salesInvoices/list/";
    }
    @GetMapping("removeInvoiceProduct/{invoiceId}/{productId}")
    public String deleteInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long productId){
        invoiceProductService.deleteInvoiceProduct(invoiceId,productId);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/list")
    public String sInvoicesList(Model model){
        model.addAttribute("invoices",invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.SALES));
        return"/invoice/sales-invoice-list";
    }
}

