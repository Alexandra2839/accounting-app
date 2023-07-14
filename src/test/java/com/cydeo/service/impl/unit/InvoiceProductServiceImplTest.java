package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.*;
import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.*;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.enums.ProductUnit;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.exception.ProductLowLimitAlertException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.cydeo.service.impl.InvoiceProductServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {InvoiceProductServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class InvoiceProductServiceImplTest {


    @InjectMocks
    InvoiceProductServiceImpl invoiceProductService;
    @Mock
    InvoiceProductRepository invoiceProductRepository;
    @Mock
    InvoiceService invoiceService;
    @Mock
    SecurityService securityService;
    @Mock
    CompanyService companyService;
    @Spy
    static
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());


    @Test
    void findById() {
        //given
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        invoiceProductDto.setId(1L);

        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        //when
        when(invoiceProductRepository.findById(invoiceProductDto.getId())).thenReturn(Optional.of(invoiceProduct));
        InvoiceProductDto actualDto = invoiceProductService.findById(1L);
        //then
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFieldsOfTypes(InvoiceDto.class)
                .isEqualTo(invoiceProductDto);
    }

    @Test
    void throw_execption_if_not_found_invoice_product() {
        Throwable throwable = catchThrowable(() -> invoiceProductService.findById(0L));
        assertInstanceOf(InvoiceProductNotFoundException.class, throwable);
        assertEquals("No such Invoice Product in the system", throwable.getMessage());
    }

    @Test
    void should_find_All_invoice_products() {
        List<InvoiceProductDto> listOfDtos = Arrays.asList(TestDocumentInitializer.getInvoiceProduct(),
                TestDocumentInitializer.getInvoiceProduct(),
                TestDocumentInitializer.getInvoiceProduct());
        List<InvoiceProduct> listOfEntities = listOfDtos.stream()
                .map(invoiceProductDto -> mapperUtil.convert(invoiceProductDto, new InvoiceProduct()))
                .collect(Collectors.toList());

        when(invoiceProductRepository.findAll()).thenReturn(listOfEntities);
        List<InvoiceProductDto> actualList = invoiceProductService.findAll();
        assertThat(actualList).usingRecursiveComparison()
                .ignoringFieldsOfTypes(InvoiceDto.class)
                .isEqualTo(listOfDtos);
    }

    @Test
    void should_save_invoice_product() {
        InvoiceProductDto dto = TestDocumentInitializer.getInvoiceProduct();
        dto.setId(1L);
        InvoiceProduct invoiceProduct = mapperUtil.convert(dto, new InvoiceProduct());


        when(invoiceService.findById(any())).thenReturn(new InvoiceDto());
        when(invoiceProductRepository.save(any(InvoiceProduct.class))).thenReturn(invoiceProduct);

        InvoiceProductDto actualDto = invoiceProductService.save(dto, 1L);
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFieldsOfTypes(InvoiceDto.class)
                .isEqualTo(dto);
    }

    @Test
    void should_delete_Invoice_Product() {
        InvoiceProductDto dto = TestDocumentInitializer.getInvoiceProduct();
        dto.setId(1L);

        InvoiceProduct invoiceProduct = mapperUtil.convert(dto, new InvoiceProduct());
        invoiceProduct.setIsDeleted(false);

        when(invoiceProductRepository.findByInvoiceIdAndId(1L, 1L)).thenReturn(invoiceProduct);
        when(invoiceProductRepository.save(any(InvoiceProduct.class))).thenReturn(invoiceProduct);


        invoiceProductService.deleteInvoiceProduct(1L, 1L);

        verify(invoiceProductRepository).findByInvoiceIdAndId((Long) any(), (Long) any());
        verify(invoiceProductRepository).save((InvoiceProduct) any());
        assertTrue(invoiceProduct.getIsDeleted());

    }


    @Test
    void testFindByInvoiceId() {
        //given
        InvoiceDto invoiceDto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        invoiceDto.setId(1L);

        List<InvoiceProduct> listOProducts = getInvoiceProducts();


        //invoiceProductRepository.save(invoiceProduct);
        //when
        when(invoiceProductRepository.findByInvoiceId(1L)).thenReturn(listOProducts);
        List<InvoiceProductDto> actualList = invoiceProductService.findByInvoiceId(1L);
        //then
        assertEquals(listOProducts.size(), actualList.size());
        verify(invoiceProductRepository).findByInvoiceId(any());
    }

    @Test
    void testIsStockNotEnough() {
        InvoiceProductDto invoiceProductDto = mock(InvoiceProductDto.class);
        when(invoiceProductDto.getProduct())
                .thenReturn(new ProductDto(1L, "Name", 1, 1, ProductUnit.LBS, new CategoryDto(), true));
        when(invoiceProductDto.getQuantity()).thenReturn(1);
        assertFalse(invoiceProductService.isStockNotEnough(invoiceProductDto));
        verify(invoiceProductDto, atLeast(1)).getProduct();
        verify(invoiceProductDto, atLeast(1)).getQuantity();
    }



    @Test
    void should_test_Check_Low_Limit() {
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        ArrayList<InvoiceProduct> invoiceProductList = new ArrayList<>();
        invoiceProductList.add(invoiceProduct);
        when(invoiceProductRepository.findByInvoiceId((Long) any())).thenReturn(invoiceProductList);
        invoiceProductService.checkLowLimit(1L);
        verify(invoiceProductRepository).findByInvoiceId((Long) any());
    }

    @Test
    void should_show_list_of_All_By_Date_And_Logged_In_User() {
        //given
        UserDto user = TestDocumentInitializer.getUser("ADMIN");
        List<InvoiceProduct> listOfEntities = getInvoiceProducts();
        //when
        when(securityService.getLoggedInUser()).thenReturn(user);
        when(invoiceProductRepository.findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc(
                any(), any())).thenReturn(listOfEntities);
        //then
        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.listAllByDateAndLoggedInUser();

        verify(invoiceProductRepository).findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc(
                any(), any());
        assertNotNull(invoiceProductDtos);

    }

    private static List<InvoiceProductDto> getInvoiceProductDtos(){
        List<InvoiceProductDto> invoiceProductDtos = new ArrayList<>();
        invoiceProductDtos.add(TestDocumentInitializer.getInvoiceProduct());
        invoiceProductDtos.add(TestDocumentInitializer.getInvoiceProduct());
        invoiceProductDtos.add(TestDocumentInitializer.getInvoiceProduct());
        invoiceProductDtos.get(0).setId(1L);
        invoiceProductDtos.get(1).setId(2L);
        invoiceProductDtos.get(2).setId(3L);
        return invoiceProductDtos;
    }
    private static List<InvoiceProduct> getInvoiceProducts(){
        List<InvoiceProduct> invoiceProducts = getInvoiceProductDtos().stream()
                .map(invoiceProductDto -> mapperUtil.convert(invoiceProductDto, new InvoiceProduct()))
                .collect(Collectors.toList());
        return invoiceProducts;
    }

}
