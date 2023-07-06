package com.cydeo.controller;

import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final InvoiceProductService invoiceProductService;

    public ReportingController(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/stockData")
    public String getStockData(Model model){

        model.addAttribute("invoiceProducts", invoiceProductService.listAllByDate());

        return "/report/stock-report";
    }

}
