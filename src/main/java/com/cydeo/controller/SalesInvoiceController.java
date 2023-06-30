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

        InvoiceDto newInvoiceDTO = new InvoiceDto();

        newInvoiceDTO.setInvoiceNo(InvoiceServiceImpl.generateInvoiceNo(InvoiceType.SALES, invoiceService.listOfAllInvoices()));
        newInvoiceDTO.setDate(LocalDate.now());


        model.addAttribute("newSalesInvoice",newInvoiceDTO);
        model.addAttribute("clients", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        return "/invoice/sales-invoice-create";
    }
    @PostMapping("/create")
    public String saveSalesInvoice(@ModelAttribute("newSalesInvoice")InvoiceDto invoiceDto, Model model){
        model.addAttribute("clients", clientVendorService.findAll());
        InvoiceDto obj1 = invoiceService.save(invoiceDto);

        return "redirect:/salesInvoices/update/"+obj1.getId();
    }
    @GetMapping("/update/{id}")//update/14
    private String editInvoice(@PathVariable Long id, Model model){


        model.addAttribute("invoice",invoiceService.findById(id));//invoice 14

        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());//invoice product taking PathVariable (14)

        model.addAttribute("clients", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id)); //all products from invoice 14

        return "/invoice/sales-invoice-update";
    }
    @PostMapping("/addInvoiceProduct/{id}")
    public String saveProduct(@ModelAttribute("newInvoiceProduct")InvoiceProductDto invoiceProductDto, @PathVariable Long id,Model model){
        // invoiceProductDto.setId(20L);//--------------------------------------------------


        invoiceProductService.save(invoiceProductDto,id);

        return "redirect:/salesInvoices/update/" + id;
    }

    @GetMapping("/list")
    public String sInvoicesList(Model model){
        model.addAttribute("invoices",invoiceService.listOfPurchasedInvoices("S"));
        return"/invoice/sales-invoice-list";
    }
}

