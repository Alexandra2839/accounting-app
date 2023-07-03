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
    private final MapperUtil mapperUtil;

    private final CurrencyClient currencyClient;

    public DashboardServiceImpl(InvoiceProductRepository invoiceProductRepository, SecurityService securityService, InvoiceRepository invoiceRepository, MapperUtil mapperUtil, CurrencyClient currencyClient) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.securityService = securityService;
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.currencyClient = currencyClient;
    }

    @Override
    public BigDecimal calculateTotalCost() {

        List<InvoiceDto> list = invoiceRepository.findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(
                        InvoiceType.PURCHASE, InvoiceStatus.APPROVED, securityService.getLoggedInUser().getCompany().getTitle())
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .map(invoiceDto -> calculateInvoiceSummary(invoiceDto))
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
                .map(invoiceDto -> calculateInvoiceSummary(invoiceDto))
                .collect(Collectors.toList());

        List<BigDecimal> sum = new ArrayList<>();
        list.forEach(invoiceDto -> sum.add(invoiceDto.getTotal()));


        return sum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private InvoiceDto calculateInvoiceSummary(InvoiceDto invoiceDto) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findByInvoiceId(invoiceDto.getId());

        BigDecimal totalPriceWithoutTax = invoiceProducts.stream()
                .map(invoiceProduct -> invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalTax = invoiceProducts.stream()
                .mapToInt(invoiceProduct -> invoiceProduct.getPrice()
                        .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()))
                        .multiply(BigDecimal.valueOf(invoiceProduct.getTax()))
                        .divide(BigDecimal.valueOf(100))
                        .intValue())
                .sum();

        BigDecimal totalPriceWithTax = totalPriceWithoutTax.add(BigDecimal.valueOf(totalTax));

        invoiceDto.setPrice(totalPriceWithoutTax);
        invoiceDto.setTax(totalTax);
        invoiceDto.setTotal(totalPriceWithTax);

        return invoiceDto;
    }
}
