package com.cydeo.service;

import java.math.BigDecimal;

public interface DashboardService {

    BigDecimal calculateTotalProfitLoss();

    BigDecimal calculateTotalCost();

    BigDecimal calculateTotalSales();
}