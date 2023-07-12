package com.cydeo.service.impl;

import com.cydeo.client.CurrencyClient;
import com.cydeo.dto.CurrencyDto;
import com.cydeo.service.DashboardService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final CurrencyClient currencyClient;

    public DashboardServiceImpl(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @Override
    public CurrencyDto getRates() {

        return currencyClient.getCurrencyRates();
    }

}
