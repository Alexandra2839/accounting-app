package com.cydeo.controller;

import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;

    public DashboardController(DashboardService dashboardService, InvoiceService invoiceService, InvoiceProductService invoiceProductService) {
        this.dashboardService = dashboardService;
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
    }


    @GetMapping
    public String getDash(Model model) {

        Map<String, BigDecimal> summaryNumbers = Map.of(
                "totalCost", invoiceService.calculateTotalCost(),
                "totalSales", invoiceService.calculateTotalSales(),
                "profitLoss", invoiceProductService.calculateTotalProfitLoss()
        );
        model.addAttribute("summaryNumbers", summaryNumbers);
        model.addAttribute("invoices", invoiceService.list3LastApprovedInvoices());
        model.addAttribute("exchangeRates", dashboardService.getRates());
        model.addAttribute("title", "Cydeo Accounting-Dashboard");
        return "dashboard";
    }
}
