package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.impl.SecurityServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InvoiceServiceImpl {
    @Autowired
    InvoiceService invoiceService;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    SecurityServiceImpl securityService;

    @Test
    void should_find_invoice_by_id() {
        // when
        InvoiceDto dto = invoiceService.findById(1L);
        // then
        assertNotNull(dto);
        assertEquals("Ower Tech", dto.getClientVendor().getClientVendorName());
    }
    @Test
    void should_throw_exception_when_invoice_not_found(){
        Throwable throwable = catchThrowable(() -> invoiceService.findById(0L));
        //then
        assertInstanceOf(InvoiceNotFoundException.class,throwable);
        assertEquals("No such invoice in the system", throwable.getMessage());

    }
    @Test
    void should_find_all_invoices(){
        List<InvoiceDto> dtos = invoiceService.listOfAllInvoices();
        Integer actualSize = dtos.size();
        Integer expected = 13;
        assertEquals(expected, actualSize);
    }

    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_save_purchase_invoice(){
        InvoiceDto invoice = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);

        InvoiceDto actualDto = invoiceService.save(invoice);
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFields("price", "id", "tax","total")
                .isEqualTo(invoice);

        assertNotNull(actualDto.getId());
        assertNotNull(actualDto.getInvoiceNo());

        Invoice invoice1 = invoiceRepository.findById(actualDto.getId()).orElseThrow();
        invoiceRepository.delete(invoice1);

    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_save_sales_invoice(){
        InvoiceDto invoice = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);

        InvoiceDto actualDto = invoiceService.saveSalesInvoice(invoice);
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFields("price", "id", "tax","total")
                .isEqualTo(invoice);

        assertNotNull(actualDto.getId());
        assertNotNull(actualDto.getInvoiceNo());

        Invoice invoice1 = invoiceRepository.findById(actualDto.getId()).orElseThrow();
        invoiceRepository.delete(invoice1);

    }
    @Test
    @Transactional
    void delete() {
        InvoiceDto invoice = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        invoice.setId(1L);
        invoiceService.delete(invoice.getId());
        Invoice invoice1 = invoiceRepository.findById(1L).orElseThrow(() -> new InvoiceNotFoundException("No such invoice in the system"));
        assertTrue(invoice1.getIsDeleted());
        invoice1.setIsDeleted(false);
        invoiceRepository.save(invoice1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_update_Invoice(){
        InvoiceDto invoice = invoiceService.findById(1L);
        invoice.setInvoiceNo("123456");
        InvoiceDto actualDto = invoiceService.update(invoice, 1L);

        assertThat(actualDto).usingRecursiveComparison()
                .isEqualTo(invoice);
    }
    @Test
    void should_not_update_and_throw_exception_when_invoice_not_found(){
        InvoiceDto invoice = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        invoice.setId(0L);
        Throwable throwable = catchThrowable(() -> invoiceService.update(invoice, 0L));
        //then
        assertInstanceOf(InvoiceNotFoundException.class,throwable);
        assertEquals("No such invoice in the system", throwable.getMessage());
    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_approve_purchase_invoice(){
        InvoiceDto invoice = invoiceService.findById(13L);
        InvoiceDto actualDto = invoiceService.approvePurchaseInvoice( 13L);

        assertNotEquals(invoice.getInvoiceStatus(), actualDto.getInvoiceStatus());
        assertEquals(actualDto.getInvoiceStatus(), InvoiceStatus.APPROVED);
    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_approve_sales_invoice(){
        InvoiceDto invoice = invoiceService.findById(4L);
        InvoiceDto actualDto = invoiceService.approveSalesInvoice( 4L);

        assertNotEquals(invoice.getInvoiceStatus(), actualDto.getInvoiceStatus());
        assertEquals(actualDto.getInvoiceStatus(), InvoiceStatus.APPROVED);
    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_generate_invoiceNo_for_new_sales_invoice(){

        InvoiceDto actualDto = invoiceService.createNewSalesInvoice();
        assertNotNull(actualDto.getInvoiceNo());
        assertTrue(actualDto.getInvoiceNo().startsWith("S-"));

    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_generate_invoiceNo_for_new_purchase_invoice(){

        InvoiceDto actualDto = invoiceService.createNewPurchasesInvoice();
        assertNotNull(actualDto.getInvoiceNo());
        assertTrue(actualDto.getInvoiceNo().startsWith("P-"));

    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_get_invoice_for_print(){
        InvoiceDto invoice = invoiceService.findById(1L);
        InvoiceDto actualDto = invoiceService.getInvoiceForPrint(1L);
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFields("price", "id", "tax","total")
                .isEqualTo(invoice);
        assertNotNull(actualDto.getTotal());
        assertNotNull(actualDto.getPrice());
        assertNotNull(actualDto.getTax());
    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com",password = "Abc1", roles = "MANAGER")
    void should_list_3_last_approved_invoices(){
        List<InvoiceDto> dtos = invoiceService.list3LastApprovedInvoices();
        Integer actualSize = dtos.size();
        Integer expected = 3;
        assertEquals(expected, actualSize);
    }

}
