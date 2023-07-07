package com.cydeo.controller;

import com.cydeo.service.InvoiceProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final InvoiceProductService invoiceProductService;

    public ReportingController(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/profitLossData")
    public String getProfitLossReport(Model model) {

        model.addAttribute("monthlyProfitLossDataMap", invoiceProductService.listMonthlyProfitLoss());

        return "report/profit-loss-report";
    }

    @GetMapping("/stockData")
    public String getStockData(Model model){

        model.addAttribute("invoiceProducts", invoiceProductService.listAllByDateAndLoggedInUser());

        return "/report/stock-report";
    }

}
