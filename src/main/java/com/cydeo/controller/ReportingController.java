package com.cydeo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    @GetMapping("/profitLossData")
    public String getProfitLossReport(Model model){



        return "report/profit-loss-report";
    }
}
