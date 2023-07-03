package com.cydeo.controller;

import com.cydeo.dto.CurrencyDto;
import com.cydeo.dto.InvoiceDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String getDash(Model model){

        // this method has only dummy info and should be modified in accordance with user stories.
        Map<String, BigDecimal> summaryNumbers = Map.of(
                "totalCost", BigDecimal.TEN,
                "totalSales", BigDecimal.TEN,
                "profitLoss", BigDecimal.ZERO
        );
        model.addAttribute("summaryNumbers", summaryNumbers);
        model.addAttribute("invoices", new ArrayList<InvoiceDto>());
        model.addAttribute("exchangeRates", new CurrencyDto());
        model.addAttribute("title", "Cydeo Accounting-Dashboard");
        return "dashboard";
    }
}
