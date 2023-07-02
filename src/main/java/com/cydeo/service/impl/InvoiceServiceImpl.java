package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final InvoiceProductRepository invoiceProductRepository;
    private final CompanyService companyService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, InvoiceProductRepository invoiceProductRepository, @Lazy CompanyService companyService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceProductRepository = invoiceProductRepository;
        this.companyService = companyService;
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public List<InvoiceDto> listOfAllInvoices() {
        List<Invoice> all = invoiceRepository.findAll();
        return  all.stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto())).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> listOfPurchasedInvoices(String s) {
        List<Invoice> allPurchased = invoiceRepository.findAllByInvoiceNoStartingWith(s);
        return allPurchased.stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto())).collect(Collectors.toList());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto) {

        invoiceDto.setCompany(companyService.getCompanyDtoByLoggedInUser());
        Invoice invoice1 = mapperUtil.convert(invoiceDto, new Invoice());
        invoice1.setId(invoiceDto.getId());
        invoice1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice1.setInvoiceType(InvoiceType.PURCHASE);
        invoiceRepository.save(invoice1);
        InvoiceDto converted = mapperUtil.convert(invoice1, new InvoiceDto());

        return converted;
    }
    public InvoiceDto saveSalesInvoice(InvoiceDto invoiceDto) {

        invoiceDto.setCompany(companyService.getCompanyDtoByLoggedInUser());

        Invoice invoice1 = mapperUtil.convert(invoiceDto, new Invoice());
        invoice1.setId(invoiceDto.getId());
        invoice1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoiceRepository.save(invoice1);
        InvoiceDto converted = mapperUtil.convert(invoice1, new InvoiceDto());

        return converted;
    }

    @Override
    public InvoiceDto delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
        return mapperUtil.convert(invoice,new InvoiceDto());
    }

    @Override
    public InvoiceDto update(InvoiceDto invoiceDto) {
        Invoice invoiceInDb = invoiceRepository.findById(invoiceDto.getId()).orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));
        Invoice convertedInvoice = mapperUtil.convert(invoiceDto, new Invoice());

        convertedInvoice.setClientVendor(invoiceInDb.getClientVendor());

        invoiceRepository.save(convertedInvoice);

        return mapperUtil.convert(convertedInvoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto approve(Long id) {
        return null;
    }

    @Override
    public InvoiceDto createNewSalesInvoice() {
        InvoiceDto invoiceDTO = new InvoiceDto();
        invoiceDTO.setInvoiceNo(String.format("S-%03d", generateInvoiceNo(InvoiceType.SALES)));
        invoiceDTO.setDate(LocalDate.now());
        return invoiceDTO;
    }

    @Override
    public InvoiceDto createNewPurchasesInvoice() {
        InvoiceDto invoiceDTO = new InvoiceDto();
        invoiceDTO.setInvoiceNo(String.format("P-%03d", generateInvoiceNo(InvoiceType.PURCHASE)));
        invoiceDTO.setDate(LocalDate.now());
        invoiceDTO.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDTO.setInvoiceType(InvoiceType.PURCHASE);
        return invoiceDTO;
    }
    private Integer generateInvoiceNo(InvoiceType invoiceType){
        List<Invoice> listOfAllInvoiceByTypeAndCompany = invoiceRepository.findByCompanyTitleAndInvoiceType(companyService.getCompanyDtoByLoggedInUser().getTitle(), invoiceType);
        return listOfAllInvoiceByTypeAndCompany.size() + 1;
    }
    public List<InvoiceDto> calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType type) {
        List<Invoice> invoices = invoiceRepository.findAllByInvoiceType(type);
        return invoices.stream().map(i -> mapperUtil.convert(i,new InvoiceDto()))
                .map(this::calculateInvoiceSummary)
                .collect(Collectors.toList());

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
