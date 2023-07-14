package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.*;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InvoiceProductServiceImplTest {
    @Autowired
    InvoiceProductService invoiceProductService;
    @Autowired
    InvoiceProductRepository invoiceProductRepository;
    @Autowired
    SecurityService securityService;

    @Test
    @Transactional
    void should_find_invoice_product_by_id(){
        //when
        InvoiceProductDto invoiceProduct = invoiceProductService.findById(1L);
        //then
        assertNotNull(invoiceProduct);
        assertEquals(5, invoiceProduct.getQuantity());

    }
    @Test
    void should_give_exception_if_invoice_product_not_found(){
        //when
        Throwable throwable = catchThrowable( () -> invoiceProductService.findById(0L));
        //then
        assertInstanceOf(InvoiceProductNotFoundException.class, throwable);
        assertEquals("No such Invoice Product in the system" , throwable.getMessage());
    }
    @Test
    @Transactional
    void should_find_all_invoice_products(){
        //when
        int size = invoiceProductService.findAll().size();
        //then
        assertEquals(15, size);
    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_save_new_invoice_product(){
        //given
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setProduct(new ProductDto());
        invoiceProductDto.setInvoice(new InvoiceDto());
        invoiceProductDto.setQuantity(5);
        invoiceProductDto.setRemainingQuantity(5);
        //when
        InvoiceProductDto savedInvoiceProduct = invoiceProductService.save(invoiceProductDto, 1L);
        //then
        assertNotNull(savedInvoiceProduct);
        assertEquals(5, savedInvoiceProduct.getQuantity());
        assertThat(savedInvoiceProduct)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(InvoiceDto.class, ProductDto.class)
                .ignoringFields("id")
                .isEqualTo(invoiceProductDto);
        //after
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(savedInvoiceProduct.getId()).orElseThrow();
        invoiceProductRepository.delete(invoiceProduct);
    }
    @Test
    @Transactional
    void should_find_product_by_invoice_id(){
        //when
        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.findByInvoiceId(1L);
        //then
        assertNotNull(invoiceProductDtos);
        assertEquals(1, invoiceProductDtos.size());
    }
    @Test
    @Transactional
    void should_delete_invoice_product(){
        //given
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        invoiceProductDto.setId(1L);

        //when
        invoiceProductService.deleteInvoiceProduct(1L, 1L);
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(1L)
                .orElseThrow( ()-> new NoSuchElementException("InvoiceProduct not found"));
        //then
        assertTrue(invoiceProduct.getIsDeleted());
        invoiceProduct.setIsDeleted(false);
        invoiceProductRepository.save(invoiceProduct);

    }
    @Test
    @Transactional
    @WithMockUser(username = "manager@greentech.com", password = "Abc1", roles = "MANAGER")
    void should_show_all_list_byDate_and_loggin_user(){
        //when
        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.listAllByDateAndLoggedInUser();
        //then
        assertNotNull(invoiceProductDtos);
        assertEquals(3, invoiceProductDtos.size());
    }
    @Test
    @Transactional
    void should_check_if_stock_is_not_enough(){
        //given
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        invoiceProductDto.setQuantity(100);
        //when
        boolean isStockNotEnough = invoiceProductService.isStockNotEnough(invoiceProductDto);
        //then
        assertTrue(isStockNotEnough);
    }
    @Test
    @Transactional
    void should_calculate_total_profit_loss_for_invoice_product(){
        //given
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceDto invoiceDto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        invoiceProductDto.setInvoice(invoiceDto);

        //when
        BigDecimal totalProfitLoss = invoiceProductService.calculateProfitLossForInvoiceProduct(invoiceProductDto);
        //then
        assertEquals(new BigDecimal("0"), totalProfitLoss);
    }


}
