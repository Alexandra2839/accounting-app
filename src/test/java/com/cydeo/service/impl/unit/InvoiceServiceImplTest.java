package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.*;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;

import com.cydeo.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @InjectMocks
    InvoiceServiceImpl invoiceService;
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    CompanyService companyService;
    @Mock
    private SecurityService securityService;

    @Spy
    static MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Test
    void findById() {
        //given
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        dto.setId(1L);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        //when
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        InvoiceDto actualDto = invoiceService.findById(1L);
        //then
        assertEquals(dto.getId(), actualDto.getId());


    }

    @Test
    void should_not_find_by_id() {
        Throwable throwable = catchThrowable(() -> invoiceService.findById(0L));
        //then
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class)
                .hasMessageContaining("No such invoice in the system");

    }
    private List<InvoiceDto> getDtos(){
        List<InvoiceDto> dtos = Arrays.asList(
                TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE),
                TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE),
                TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE)
        );
        dtos.get(0).setId(1L);
        dtos.get(1).setId(2L);
        dtos.get(2).setId(3L);
        return dtos;
    }
    private List<Invoice> getEntities(){
        List<Invoice> expectedList = getDtos().stream()
                .map(dto -> mapperUtil.convert(dto, new Invoice()))
                .collect(Collectors.toList());
        expectedList.get(0).setId(1L);
        expectedList.get(1).setId(2L);
        expectedList.get(2).setId(3L);
        return expectedList;
    }
    @Test
    void should_find_all() {
        //given
        List<Invoice> expectedList = getEntities();
        List<InvoiceDto> dtos = getDtos();
        //when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(invoiceRepository.findAll()).thenReturn(expectedList);
        List<InvoiceDto> actualList = invoiceService.listOfAllInvoices();
        //then
        //assertThat(actualList).usingRecursiveComparison().isEqualTo(dtos);
        assertEquals(dtos.size(), actualList.size());
        assertEquals(dtos.get(0).getId(), actualList.get(0).getId());
        assertEquals(dtos.get(1).getId(), actualList.get(1).getId());
        assertEquals(dtos.get(2).getId(), actualList.get(2).getId());

    }
    @Test
    void should_save_invoice(){
        //given
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        dto.setId(1L);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        //when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        InvoiceDto actualDto = invoiceService.save(dto);
        //then
        assertEquals(dto.getId(), actualDto.getId());
    }
    @Test
    void should_save_Sales_invoice(){
        //given
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        dto.setId(1L);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        //when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        InvoiceDto actualDto = invoiceService.save(dto);
        //then
        assertEquals(dto.getId(), actualDto.getId());
    }
    @Test
    void should_soft_delete_invoice(){
        //given
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        dto.setId(1L);
        dto.setCompany(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setIsDeleted(false);

        //when
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        Throwable throwable = catchThrowable(() -> invoiceService.delete(dto.getId()));
        //then
        assertTrue(invoice.getIsDeleted());

    }
    @Test
    void should_approve_purchase_invoice(){
        //given
        InvoiceProductDto productDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        dto.setId(1L);
        productDto.setInvoice(dto);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());

        //when
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        InvoiceDto actualDto = invoiceService.approvePurchaseInvoice(dto.getId());
        //then
        assertEquals(InvoiceStatus.APPROVED, actualDto.getInvoiceStatus());

    }
    @Test
    void should_approve_sales_invoice(){
        //given
        InvoiceProductDto productDto = TestDocumentInitializer.getInvoiceProduct();

        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        dto.setId(1L);
        productDto.setInvoice(dto);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setInvoiceProductList(Arrays.asList(mapperUtil.convert(productDto, new InvoiceProduct())));

        //when
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        InvoiceDto actualDto = invoiceService.approveSalesInvoice(dto.getId());
        //then
        assertEquals(InvoiceStatus.APPROVED, actualDto.getInvoiceStatus());

    }
    @Test
    void create_new_sales_invoice(){
        //given
        InvoiceProductDto productDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        dto.setId(1L);
        productDto.setInvoice(dto);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setInvoiceProductList(Arrays.asList(mapperUtil.convert(productDto, new InvoiceProduct())));

        //when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        InvoiceDto actualDto = invoiceService.createNewSalesInvoice();
        //then
        assertNotEquals(dto.getInvoiceNo(), actualDto.getInvoiceNo());
        //cuz in TestDocumentation Invoice Created manually with different invoiceNo

    }
    @Test
    void should_get_invoice_for_print(){
        //given
        InvoiceProductDto productDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        dto.setId(1L);
        productDto.setInvoice(dto);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setInvoiceProductList(Arrays.asList(mapperUtil.convert(productDto, new InvoiceProduct())));

        //when
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        InvoiceDto actualDto = invoiceService.getInvoiceForPrint(dto.getId());
        //then
        assertEquals(dto.getId(), actualDto.getId());
    }
    @Test
    void should_list_3_Last_Approved_Invoices(){
        when(invoiceRepository.findTop3ByCompanyTitleAndInvoiceStatusOrderByDateDesc((String) any(),
                (InvoiceStatus) any())).thenReturn(new ArrayList<>());

        UserDto userDto = new UserDto();
        userDto.setCompany(new CompanyDto());
        when(securityService.getLoggedInUser()).thenReturn(userDto);

        assertTrue(invoiceService.list3LastApprovedInvoices().isEmpty());
        verify(invoiceRepository).findTop3ByCompanyTitleAndInvoiceStatusOrderByDateDesc((String) any(),
                (InvoiceStatus) any());
        verify(securityService).getLoggedInUser();
//        //given
//        List<Invoice> expectedList = getApprovedEntities();
//        List<InvoiceDto> dtos = getApprovedDtos();
//        //when
//        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
//        when(invoiceRepository.findTop3ByCompanyTitleAndInvoiceStatusOrderByDateDesc(companyService
//                .getCompanyDtoByLoggedInUser().
//                getTitle(), InvoiceStatus.APPROVED)).thenReturn(expectedList);
//        when(securityService.getLoggedInUser()).thenReturn(TestDocumentInitializer.getUser("ADMIN"));
//        List<InvoiceDto> actualList = invoiceService.list3LastApprovedInvoices();
//        //then
//        assertEquals(3, actualList.size());
//        assertEquals(dtos.get(0).getInvoiceStatus(), actualList.get(0).getInvoiceStatus());
//        assertEquals(dtos.get(1).getId(), actualList.get(1).getId());
//        assertEquals(dtos.get(2).getId(), actualList.get(2).getId());

    }
    private List<InvoiceDto> getApprovedDtos(){
        List<InvoiceDto> dtos = Arrays.asList(
                TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE),
                TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE),
                TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE)
        );
        dtos.get(0).setId(1L);
        dtos.get(1).setId(2L);
        dtos.get(2).setId(3L);
        return dtos;
    }
    private List<Invoice> getApprovedEntities() {
        List<Invoice> expectedList = getApprovedDtos().stream()
                .map(dto -> mapperUtil.convert(dto, new Invoice()))
                .collect(Collectors.toList());
        expectedList.get(0).setId(1L);
        expectedList.get(1).setId(2L);
        expectedList.get(2).setId(3L);
        return expectedList;

    }
}