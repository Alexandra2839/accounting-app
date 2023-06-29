package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {

    InvoiceDto findById(Long id);
    List<InvoiceDto> listOfAllInvoices();
    List<InvoiceDto> listOfPurchasedInvoices(String s);
    InvoiceDto update(InvoiceDto invoiceDto);
}
