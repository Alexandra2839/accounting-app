package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final MapperUtil mapperUtil;
    private final InvoiceProductRepository invoiceProductRepository;
    private final CompanyService companyService;
    private final ProductService productService;
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProductService invoiceProductService, MapperUtil mapperUtil, InvoiceProductRepository invoiceProductRepository, CompanyService companyService, ProductService productService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.mapperUtil = mapperUtil;
        this.invoiceProductRepository = invoiceProductRepository;
        this.companyService = companyService;
        this.productService = productService;

    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));
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
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto update(InvoiceDto invoiceDto, Long id) {//apple
        Invoice invoiceInDb = invoiceRepository.findById(id)//orange
                .orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));


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
        Invoice invoiceDB = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such element in the system"));

        List<InvoiceProduct> listOfInvoiceProductByInvoiceId = invoiceProductRepository.findByInvoiceId(id);

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

    public InvoiceDto approve(Long id) {
        Invoice invoiceDB = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such element in the system"));

        invoiceDB.setInvoiceStatus(InvoiceStatus.APPROVED);

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
}
