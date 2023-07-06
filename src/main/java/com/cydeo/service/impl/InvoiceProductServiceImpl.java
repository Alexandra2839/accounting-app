package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository,@Lazy InvoiceService invoiceService, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id).orElseThrow(() -> new NoSuchElementException());

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
    public List<InvoiceProductDto> findByInvoiceId(Long id) {
        return invoiceProductRepository.findByInvoiceId(id)
                .stream()
                .map(this::calculateTotalPrice)
                .collect(Collectors.toList());
    }

    private InvoiceProductDto calculateTotalPrice(InvoiceProduct invoiceProduct) {
        /**
         * this method calculate Total price of Invoice Product inside the invoice
         */

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
        /**
         * Check if we have enough products to sell
         */
        if(invoiceProductDTO.getQuantity()==null|| invoiceProductDTO.getProduct().getQuantityInStock()==null)
            return false;
        return invoiceProductDTO.getQuantity() > invoiceProductDTO.getProduct().getQuantityInStock();
    }
}
