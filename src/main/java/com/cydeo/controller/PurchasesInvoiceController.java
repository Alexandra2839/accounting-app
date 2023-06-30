package com.cydeo.controller;


import com.cydeo.dto.InvoiceDto;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.impl.InvoiceProductServiceImpl;
import com.cydeo.service.impl.InvoiceServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;


    public PurchasesInvoiceController(InvoiceService invoiceService,ProductService productService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }

    @GetMapping("/create")
    public String createPurchaseInvoice(Model model){

        InvoiceDto newInvoiceDTO = new InvoiceDto();

        newInvoiceDTO.setInvoiceNo(InvoiceServiceImpl.generateInvoiceNo(InvoiceType.PURCHASE, invoiceService.listOfAllInvoices()));
        newInvoiceDTO.setDate(LocalDate.now());


        model.addAttribute("newPurchaseInvoice",newInvoiceDTO);
        model.addAttribute("vendors", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        return "/invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String savePurchaseInvoice(@ModelAttribute("newPurchaseInvoice")InvoiceDto invoiceDto, Model model){
        model.addAttribute("vendors", clientVendorService.findAll());
        InvoiceDto obj1 = invoiceService.save(invoiceDto);

        return "redirect:/purchaseInvoices/update/"+obj1.getId();
    }
    @GetMapping("/update/{id}")//update/14
    private String editInvoice(@PathVariable Long id, Model model){

       model.addAttribute("invoice",invoiceService.findById(id));//invoice 14

        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());//invoice product taking PathVariable (14)

        model.addAttribute("vendors", clientVendorService.findAll());
        model.addAttribute("products", productService.listAllProducts());

        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id)); //all products from invoice 14

        return "/invoice/purchase-invoice-update";
    }

    @PostMapping("/addInvoiceProduct/{id}")
    public String saveProduct(@ModelAttribute("newInvoiceProduct")InvoiceProductDto invoiceProductDto, @PathVariable Long id,Model model){



       invoiceProductService.save(invoiceProductDto,id);

       return "redirect:/purchaseInvoices/update/" + id;
    }


    @GetMapping("/list")
    public String pInvoicesList(Model model){
        model.addAttribute("invoices",invoiceService.listOfPurchasedInvoices("P"));
        return"/invoice/purchase-invoice-list";
    }




}
