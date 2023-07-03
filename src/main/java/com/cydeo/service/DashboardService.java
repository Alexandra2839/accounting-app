package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;

import com.cydeo.dto.CurrencyDto;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

    BigDecimal calculateTotalProfitLoss();

    BigDecimal calculateTotalCost();

    BigDecimal calculateTotalSales();

    List<InvoiceDto> list3LastApprovedInvoices();

    CurrencyDto getRates();
}