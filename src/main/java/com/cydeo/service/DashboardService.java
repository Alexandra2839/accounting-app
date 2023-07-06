package com.cydeo.service;

import com.cydeo.dto.CurrencyDto;
import com.cydeo.dto.InvoiceDto;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

    CurrencyDto getRates();
}