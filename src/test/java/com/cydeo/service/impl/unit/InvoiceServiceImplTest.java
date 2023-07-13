package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.*;
import com.cydeo.entity.*;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.ProductNotFoundException;
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
    @Mock
    private InvoiceProductService invoiceProductService;

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
        Invoice invoice = TestDocumentInitializer.getInvoiceEntity(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(invoice);

        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        assertEquals(1, invoiceService.listOfAllInvoices().size());
        verify(invoiceRepository).findAll();
        verify(mapperUtil).convert((Object) any(), (InvoiceDto) any());

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
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE);
        dto.setId(1L);
        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.findByInvoiceId(dto.getId());
        List<InvoiceProduct> invoiceProducts = invoiceProductDtos.stream()
                .map(invoiceProductDto -> mapperUtil.convert(invoiceProductDto, new InvoiceProduct()))
                .collect(Collectors.toList());

        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setInvoiceProductList(invoiceProducts);
        //when
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        InvoiceDto actualDto = invoiceService.approvePurchaseInvoice(dto.getId());
        //then
        assertEquals(InvoiceStatus.APPROVED, actualDto.getInvoiceStatus());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));


    }
    @Test
    void should_approve_sales_invoice(){
        //given
        InvoiceDto dto = TestDocumentInitializer.getInvoice(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.SALES);
        dto.setId(1L);
        Invoice invoice = mapperUtil.convert(dto, new Invoice());
        invoice.setInvoiceProductList(new ArrayList<>());
        //when
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        InvoiceDto actualDto = invoiceService.approveSalesInvoice(dto.getId());
        //then
        assertEquals(InvoiceStatus.APPROVED, actualDto.getInvoiceStatus());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));

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

    }
    @Test
    void testCalculateInvoiceSummariesAndShowInvoiceListByType() {
        when(invoiceRepository.findByCompanyTitleAndInvoiceTypeSorted(any(), any()))
                .thenReturn(new ArrayList<>());
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(new CompanyDto());

        assertTrue(invoiceService.calculateInvoiceSummariesAndShowInvoiceListByType(InvoiceType.PURCHASE).isEmpty());
        verify(invoiceRepository).findByCompanyTitleAndInvoiceTypeSorted(any(), any());
        verify(companyService).getCompanyDtoByLoggedInUser();
    }
  @Test
  void should_calculate_total_coast(){
        //given
      when(invoiceRepository.findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(any(),
              any(), any())).thenReturn(new ArrayList<>());
      UserDto user = TestDocumentInitializer.getUser("admin");
      user.setCompany(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));

      when(securityService.getLoggedInUser()).thenReturn(user);
      BigDecimal actualCalculateTotalCostResult = invoiceService.calculateTotalCost();
      assertSame(actualCalculateTotalCostResult.ZERO, actualCalculateTotalCostResult);
      assertEquals("0", actualCalculateTotalCostResult.toString());
      verify(invoiceRepository).findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(any(),
              any(), any());
      verify(securityService).getLoggedInUser();

  }
  @Test
  void should_get_current_company(){
        //given
      CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE);
      when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(companyDto);
      assertSame(companyDto, invoiceService.getCurrentCompany());
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