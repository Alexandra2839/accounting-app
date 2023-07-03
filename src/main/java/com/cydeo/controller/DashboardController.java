package com.cydeo.controller;

import com.cydeo.dto.CurrencyDto;
import com.cydeo.service.DashboardService;
import com.cydeo.dto.InvoiceDto;
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

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String getDash(Model model){

        Map<String, BigDecimal> summaryNumbers = Map.of(
                "totalCost", dashboardService.calculateTotalCost(),
                "totalSales", dashboardService.calculateTotalSales(),
                "profitLoss", dashboardService.calculateTotalProfitLoss()
        );
        model.addAttribute("summaryNumbers", summaryNumbers);
        model.addAttribute("invoices", dashboardService.list3LastApprovedInvoices());
        model.addAttribute("exchangeRates", dashboardService.getRates());
        model.addAttribute("title", "Cydeo Accounting-Dashboard");
        return "dashboard";
    }
}
