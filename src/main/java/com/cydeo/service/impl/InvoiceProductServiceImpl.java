package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, @Lazy InvoiceService invoiceService, MapperUtil mapperUtil, SecurityService securityService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id).orElseThrow(() -> new InvoiceProductNotFoundException("No such Invoice Product in the system"));

        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAll() {
        List<InvoiceProduct> invoiceProduct = invoiceProductRepository.findAll();
        return invoiceProduct.stream().map(i -> mapperUtil.convert(i, new InvoiceProductDto())).collect(Collectors.toList());
    }


    @Override
    public InvoiceProductDto save(InvoiceProductDto invoiceProductDto, Long id) {
        InvoiceDto invoiceDto = invoiceService.findById(id);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());

        invoiceProduct.setInvoice(invoice);

        InvoiceProduct savedInvoiceProduct = invoiceProductRepository.save(invoiceProduct);

        return mapperUtil.convert(savedInvoiceProduct, new InvoiceProductDto());
    }

    @Override
    public InvoiceProductDto deleteInvoiceProduct(Long invoiceId, Long productId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findByInvoiceIdAndId(invoiceId, productId);
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> listAllByDateAndLoggedInUser() {

        UserDto loggedInUser = securityService.getLoggedInUser();

        Company loggedInUserCompany = mapperUtil.convert(loggedInUser.getCompany(), new Company());

        List<InvoiceProduct> invoiceProducts =
                invoiceProductRepository.findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc
                        (InvoiceStatus.APPROVED, loggedInUserCompany);
        return invoiceProducts.stream().map(ip->mapperUtil.convert(ip, new InvoiceProductDto())).collect(Collectors.toList());

    }

    @Override
    public List<InvoiceProductDto> findByInvoiceId(Long id) {
        return invoiceProductRepository.findByInvoiceId(id)
                .stream()
                .map(this::calculateTotalPrice)
                .collect(Collectors.toList());
    }

    private InvoiceProductDto calculateTotalPrice(InvoiceProduct invoiceProduct) {

        InvoiceProductDto invoiceProductDTO = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());

        BigDecimal quantity = new BigDecimal(invoiceProductDTO.getQuantity());
        BigDecimal tax = invoiceProductDTO.getTax();
        BigDecimal totalAmountWithoutTax = invoiceProductDTO.getPrice().multiply(quantity);
        BigDecimal totalAmountWithTax = totalAmountWithoutTax.add(totalAmountWithoutTax.multiply(tax).divide(new BigDecimal(100)));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedTotalAmountWithTax = decimalFormat.format(totalAmountWithTax);
        BigDecimal totalAmountWithTaxValue = new BigDecimal(formattedTotalAmountWithTax);

        invoiceProductDTO.setTotal(totalAmountWithTaxValue);

        return invoiceProductDTO;
    }

    @Override
    public boolean isStockNotEnough(InvoiceProductDto invoiceProductDTO) {
        if (invoiceProductDTO.getQuantity() == null || invoiceProductDTO.getProduct().getQuantityInStock() == null)
            return true;
        return invoiceProductDTO.getQuantity() > invoiceProductDTO.getProduct().getQuantityInStock();
    }

    @Override
    public BigDecimal calculateTotalProfitLoss() {

        List<InvoiceProduct> list = invoiceProductRepository.findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyTitle(
                InvoiceStatus.APPROVED, InvoiceType.SALES, securityService.getLoggedInUser().getCompany().getTitle());

        List<BigDecimal> sum = new ArrayList<>();
        list.forEach(invoiceProductDto -> sum.add(invoiceProductDto.getProfitLoss()));

        return sum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> listMonthlyProfitLoss() {

        Map<String, BigDecimal> monthlyProfitLoss = new HashMap<>();

        List<InvoiceProduct> list2 = invoiceProductRepository
                .findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyTitle(InvoiceStatus.APPROVED, InvoiceType.SALES, securityService.getLoggedInUser().getCompany().getTitle());

        if (list2.size() == 0) return new HashMap<String, BigDecimal>();

        for (InvoiceProduct invoiceProduct : list2) {
            monthlyProfitLoss.put(invoiceProduct.getInvoice().getDate().getMonth().toString(), null);
        }

        Set<String> months = monthlyProfitLoss.keySet();
        int i = 0;

        for (String month : months) {
            List<BigDecimal> sum2 = new ArrayList<>();

            list2.stream().filter(list -> list.getInvoice().getDate().getMonth().toString().equals(month))
                    .forEach(invoiceProductDto -> sum2.add(invoiceProductDto.getProfitLoss()));

            monthlyProfitLoss.put(list2.get(i).getInvoice().getDate().getMonth().toString(), sum2.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            i++;
        }

        return monthlyProfitLoss;
    }

}
