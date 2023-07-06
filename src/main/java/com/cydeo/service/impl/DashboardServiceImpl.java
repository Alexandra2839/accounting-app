package com.cydeo.service.impl;

import com.cydeo.client.CurrencyClient;
import com.cydeo.dto.CurrencyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final SecurityService securityService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;

    private final CurrencyClient currencyClient;

    public DashboardServiceImpl(InvoiceProductRepository invoiceProductRepository, SecurityService securityService, InvoiceRepository invoiceRepository, InvoiceService invoiceService, CurrencyClient currencyClient, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.securityService = securityService;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
        this.currencyClient = currencyClient;
    }

    @Override
    public BigDecimal calculateTotalCost() {

        List<InvoiceDto> list = invoiceRepository.findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(
                        InvoiceType.PURCHASE, InvoiceStatus.APPROVED, securityService.getLoggedInUser().getCompany().getTitle())
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .map(invoiceDto -> invoiceService.calculateInvoiceSummary(invoiceDto))
                .collect(Collectors.toList());

        List<BigDecimal> sum = new ArrayList<>();
        list.forEach(invoiceDto -> sum.add(invoiceDto.getTotal()));


        return sum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    @Override
    public BigDecimal calculateTotalSales() {
        List<InvoiceDto> list = invoiceRepository.findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(
                        InvoiceType.SALES, InvoiceStatus.APPROVED, securityService.getLoggedInUser().getCompany().getTitle())
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .map(invoiceDto -> invoiceService.calculateInvoiceSummary(invoiceDto))
                .collect(Collectors.toList());

        List<BigDecimal> sum = new ArrayList<>();
        list.forEach(invoiceDto -> sum.add(invoiceDto.getTotal()));


        return sum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<InvoiceDto> list3LastApprovedInvoices() {
        return invoiceRepository.findTop3ByCompanyTitleAndInvoiceStatusOrderByDateDesc(
                        securityService.getLoggedInUser().getCompany().getTitle(), InvoiceStatus.APPROVED)
                .stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .map(invoiceDto -> invoiceService.calculateInvoiceSummary(invoiceDto))
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyDto getRates() {

        return currencyClient.getCurrencyRates();
    }

    @Override
    public BigDecimal calculateTotalProfitLoss() {

        List<InvoiceProduct> list = invoiceProductRepository.findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyTitle(
                InvoiceStatus.APPROVED, InvoiceType.SALES, securityService.getLoggedInUser().getCompany().getTitle());

        List<BigDecimal> sum = new ArrayList<>();
        list.forEach(invoiceProductDto -> sum.add(invoiceProductDto.getProfitLoss()));

        return sum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
