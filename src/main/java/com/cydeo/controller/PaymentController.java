package com.cydeo.controller;

import com.cydeo.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping({"/list","/list/{year}"})
    public String showMonthsList(@RequestParam(value = "year", required = false)String year, Model model){
        int yearForMethods = LocalDate.now().getYear();
        if(year != null && !year.isEmpty()){
            yearForMethods = Integer.parseInt(year);
        }
        paymentService.findPaymentsIfNotExist(yearForMethods);
        model.addAttribute("payments",paymentService.findAllByYear(yearForMethods));
        model.addAttribute("year", yearForMethods);
        return "/payment/payment-list";
    }
    @GetMapping("/newpayment/{id}")
    public String payButton(@PathVariable Long id,Model model){
        model.addAttribute("payment", paymentService.getPaymentById(id));
        return"/payment/payment-method";
    }

}
