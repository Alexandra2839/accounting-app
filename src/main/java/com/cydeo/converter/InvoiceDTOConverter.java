package com.cydeo.converter;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.InvoiceService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;

@Controller
public class InvoiceDTOConverter implements Converter<String, InvoiceDto> {
    private final InvoiceService invoiceService;

    public InvoiceDTOConverter(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public InvoiceDto convert(String source) {

        if (source == null || source.equals("")) {
            return null;
        }

        return invoiceService.findById(Long.parseLong(source));
    }
}
