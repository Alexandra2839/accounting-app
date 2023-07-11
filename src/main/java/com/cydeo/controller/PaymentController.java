package com.cydeo.controller;

import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.ChargeRequest;
import com.cydeo.service.PaymentService;
import com.cydeo.service.impl.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    @Value("pk_test_51NS6IJHtE959pxoPuS0nkKo8FrBLOef4lcHbTfeeuVPHEWXWY1NZA2wlK2b645z2l18OeS05AHLdgansZKy5tiqa00q7XRhAHd")
    private String stripePublicKey;
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService, StripeService stripeService) {
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
        PaymentDto payment = paymentService.getPaymentById(id);
        model.addAttribute("payment", payment);
        model.addAttribute("amount",payment.getAmount() );
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.USD);
        model.addAttribute("monthId", id);
        return"/payment/payment-method";
    }
    @PostMapping("/charge/{id}")
    public String charge(@PathVariable("id") Long id, ChargeRequest chargeRequest, Model model)
            throws StripeException {

        chargeRequest.setDescription("Cydeo accounting subscription fee for : " +
                paymentService.getPaymentById(id).getMonth() + " " +
                paymentService.getPaymentById(id).getYear());
        chargeRequest.setCurrency(ChargeRequest.Currency.USD);
        chargeRequest.setAmount("250");

        Charge charge = StripeService.charge(chargeRequest);

        model.addAttribute("chargeId", charge.getId());
        model.addAttribute("description", charge.getDescription());

        PaymentDto paidPayment = paymentService.getPaymentById(id);
        paidPayment.setPaid(true);
        paidPayment.setCompanyStripedId(charge.getId());
        paidPayment.setPaymentDate(LocalDate.now());

        paymentService.update(paidPayment);

        return "/payment/payment-result";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "/payment/payment-result";
    }

    @GetMapping("toInvoice/{id}")
    public String printMembershipInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("company", paymentService.getPaymentById(id).getCompany());
        model.addAttribute("payment", paymentService.getPaymentById(id));
        return "/payment/payment-invoice-print";
    }

}
