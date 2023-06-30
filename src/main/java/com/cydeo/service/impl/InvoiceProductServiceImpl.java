package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, InvoiceRepository invoiceRepository, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceRepository = invoiceRepository;
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
    public InvoiceProductDto save(InvoiceProductDto invoiceProductDto, Long id) {// new Product ,  invoiceDTO 14
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such invoice in the system"));

        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());

        invoiceProduct.setInvoice(invoice);

        invoiceProductRepository.save(invoiceProduct);

        InvoiceProductDto savedInvoiceProduct = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());

//        BigDecimal quantity = new BigDecimal(savedInvoiceProduct.getQuantity());
//        BigDecimal tax = new BigDecimal(savedInvoiceProduct.getTax());
//
//
//        BigDecimal totalAmountWithOutTax = savedInvoiceProduct.getPrice().multiply(quantity) ;
//
//        savedInvoiceProduct.setTotal(totalAmountWithOutTax.add ( (totalAmountWithOutTax.multiply(tax)).divide(new BigDecimal(100)) ));

        return savedInvoiceProduct;
    }

    @Override
    public List<InvoiceProductDto> findByInvoiceId(Long id) {
        return invoiceProductRepository.findByInvoiceId(id).stream().map(p -> mapperUtil.convert(p, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }
}
