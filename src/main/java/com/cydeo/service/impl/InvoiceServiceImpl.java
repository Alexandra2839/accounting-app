package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
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

        Invoice invoice1 = mapperUtil.convert(invoiceDto, new Invoice());
        invoice1.setId(invoiceDto.getId());
        invoiceRepository.save(invoice1);
        return mapperUtil.convert(invoice1,new InvoiceDto());
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
        invoiceDTO.setInvoiceNo("S-00" + (invoiceRepository.findAllByInvoiceType(InvoiceType.SALES).size() + 1));
        invoiceDTO.setDate(LocalDate.now());
        return invoiceDTO;
    }

    @Override
    public InvoiceDto createNewPurchasesInvoice() {
        InvoiceDto invoiceDTO = new InvoiceDto();
        invoiceDTO.setInvoiceNo("P-00" + (invoiceRepository.findAllByInvoiceType(InvoiceType.PURCHASE).size() + 1));
        invoiceDTO.setDate(LocalDate.now());
        invoiceDTO.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDTO.setInvoiceType(InvoiceType.PURCHASE);
        return invoiceDTO;
    }
    //    public static String generateInvoiceNo(InvoiceType type,List<InvoiceDto> list){
//
//        int invoiceCounter = 1;
//        String prefix = "";
//
//        if (type.equals(InvoiceType.PURCHASE)) {
//            prefix = "P-";
//        } else if (type.equals(InvoiceType.SALES)) {
//            prefix = "S-";
//        } else {
//            return null;
//        }
//
//        while (true) {
//            String formattedCounter = String.format("%03d", invoiceCounter);
//            String invoiceNo = prefix + formattedCounter;
//
//            if (!list.stream().anyMatch(l -> l.getInvoiceNo().equals(invoiceNo))) {
//                return invoiceNo;
//            }
//
//            invoiceCounter++;
//        }
//    }
}
