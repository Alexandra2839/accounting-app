package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final ProductService productService;

    private final SecurityService securityService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProductService invoiceProductService, MapperUtil mapperUtil, CompanyService companyService, ProductService productService, SecurityService securityService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.productService = productService;

        this.securityService = securityService;
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("No such invoice in the system"));
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public List<InvoiceDto> listOfAllInvoices() {
        List<Invoice> all = invoiceRepository.findAll();
        return all.stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto())).collect(Collectors.toList());
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
        invoice1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice1.setInvoiceType(InvoiceType.PURCHASE);
        invoiceRepository.save(invoice1);
        InvoiceDto converted = mapperUtil.convert(invoice1, new InvoiceDto());

        return converted;
    }

    public InvoiceDto saveSalesInvoice(InvoiceDto invoiceDto) {

        invoiceDto.setCompany(companyService.getCompanyDtoByLoggedInUser());

        Invoice invoice1 = mapperUtil.convert(invoiceDto, new Invoice());

        invoice1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoiceRepository.save(invoice1);
        InvoiceDto converted = mapperUtil.convert(invoice1, new InvoiceDto());

        return converted;
    }

    @Override
    public InvoiceDto delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("No such invoice in the system"));
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto update(InvoiceDto invoiceDto, Long id) {//apple
        Invoice invoiceInDb = invoiceRepository.findById(id)//orange
                .orElseThrow(() -> new InvoiceNotFoundException("No such invoice in the system"));


        Invoice convertedInvoice = mapperUtil.convert(invoiceDto, new Invoice());//apple
        //orange      =====              apple
        convertedInvoice.setId(invoiceInDb.getId());
        convertedInvoice.setInvoiceStatus(invoiceInDb.getInvoiceStatus());
        convertedInvoice.setCompany(invoiceInDb.getCompany());
        convertedInvoice.setInvoiceType(invoiceInDb.getInvoiceType());

        Invoice save = invoiceRepository.save(convertedInvoice);//orange

        return mapperUtil.convert(save, new InvoiceDto());//orange
    }

    @Override
    public InvoiceDto approvePurchaseInvoice(Long id) {
        Invoice invoiceDB = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("No such element in the system"));

        List<InvoiceProductDto> listOfInvoiceProductDtoByInvoiceId = invoiceProductService.findByInvoiceId(id);
        List<InvoiceProduct> listOfInvoiceProductByInvoiceId = listOfInvoiceProductDtoByInvoiceId.stream()
                .map(invoiceProductDto -> mapperUtil.convert(invoiceProductDto, new InvoiceProduct()))
                .collect(Collectors.toList());

        for (InvoiceProduct invoiceProduct : listOfInvoiceProductByInvoiceId) {

            ProductDto product = productService.findProductById(invoiceProduct.getProduct().getId());
            Product productEntity = mapperUtil.convert(product, new Product());

            productEntity.setQuantityInStock(productEntity.getQuantityInStock() + invoiceProduct.getQuantity());
            productEntity.setId(product.getId());
            ProductDto productDto = mapperUtil.convert(productEntity, new ProductDto());

            productService.createProduct(productDto);
        }

        invoiceDB.setInvoiceStatus(InvoiceStatus.APPROVED);

        invoiceRepository.save(invoiceDB);
        return mapperUtil.convert(invoiceDB, new InvoiceDto());
    }

    public InvoiceDto approveSalesInvoice(Long id) {
        Invoice invoiceDB = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("No such element in the system"));


        invoiceDB.getInvoiceProductList().stream().map(
                        l -> {
                            l.getProduct().setQuantityInStock(l.getProduct().getQuantityInStock() - l.getQuantity());
                            l.setProfitLoss(invoiceProductService.calculateProfitLossForInvoiceProduct(mapperUtil.convert(l, new InvoiceProductDto())));
                            return l;
                        })
                .collect(Collectors.toList());
        invoiceDB.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDB.setDate(LocalDate.now());

        invoiceRepository.save(invoiceDB);
        return mapperUtil.convert(invoiceDB, new InvoiceDto());
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

    private Integer generateInvoiceNo(InvoiceType invoiceType) {
        List<Invoice> listOfAllInvoiceByTypeAndCompany = invoiceRepository.findByCompanyTitleAndInvoiceType(companyService.getCompanyDtoByLoggedInUser().getTitle(), invoiceType);
        return listOfAllInvoiceByTypeAndCompany.size() + 1;
    }

    public List<InvoiceDto> calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType type) {
        List<Invoice> invoices = invoiceRepository.findByCompanyTitleAndInvoiceTypeSorted(companyService.getCompanyDtoByLoggedInUser().getTitle(), type);
        return invoices.stream().map(i -> mapperUtil.convert(i, new InvoiceDto()))
                .map(this::calculateInvoiceSummary)
                .collect(Collectors.toList());

    }


    public InvoiceDto calculateInvoiceSummary(InvoiceDto invoiceDto) {
        /**
         * Looking InvoiceDto by ID
         * and do math operating for Price Wit Out Tax, Tax and Total Price With Taxed
         */
        InvoiceDto invoiceDto1 = findById(invoiceDto.getId());
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findByInvoiceId(invoiceDto1.getId());

        BigDecimal totalPriceWithoutTax = invoiceProducts.stream()
                .map(invoiceProduct -> invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = invoiceProducts.stream()
                .map(invoiceProduct -> invoiceProduct.getPrice()
                        .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()))
                        .multiply(invoiceProduct.getTax())
                        .divide(BigDecimal.valueOf(100)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String formattedTotalTax = decimalFormat.format(totalTax);
        BigDecimal totalPriceWithTax = totalPriceWithoutTax.add(totalTax);
        String formattedTotal = decimalFormat.format(totalPriceWithTax);

        BigDecimal taxValue = new BigDecimal(formattedTotalTax);
        BigDecimal totalValue = new BigDecimal(formattedTotal);

        invoiceDto1.setPrice(totalPriceWithoutTax);
        invoiceDto1.setTax(taxValue);
        invoiceDto1.setTotal(totalValue);

        return invoiceDto1;
    }

    @Override
    public InvoiceDto getInvoiceForPrint(Long id) {
        InvoiceDto invoiceDto = findById(id);
        /**
         * this method preparing my InvoiceDto for printing
         */

        InvoiceDto invoiceDto1 = calculateInvoiceSummary(invoiceDto);
        return invoiceDto1;
    }

    public CompanyDto getCurrentCompany() {
        /**
         * This method I use to send current company information to my controller
         */
        CompanyDto company = companyService.getCompanyDtoByLoggedInUser();
        return company;
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
    public List<InvoiceDto> list3LastApprovedInvoices() {
        return invoiceRepository.findTop3ByCompanyTitleAndInvoiceStatusOrderByDateDesc(
                        securityService.getLoggedInUser().getCompany().getTitle(), InvoiceStatus.APPROVED)
                .stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .map(invoiceDto -> calculateInvoiceSummary(invoiceDto))
                .collect(Collectors.toList());
    }
}
