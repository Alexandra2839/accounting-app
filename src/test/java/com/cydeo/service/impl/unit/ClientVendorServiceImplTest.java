package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.ClientVendorNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.impl.ClientVendorServiceImpl;
import com.cydeo.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientVendorServiceImplTest {

    @InjectMocks
    ClientVendorServiceImpl clientVendorService;

    @Mock
    ClientVendorRepository clientVendorRepository;

    @Mock
    CompanyServiceImpl companyService;

    @Spy
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Test
    void should_find_clientVendor_Id() {
        // given
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        dto.setId(1L);
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());

        // when
        when(clientVendorRepository.findById(dto.getId())).thenReturn(Optional.of(clientVendor));

        ClientVendorDto actualDto = clientVendorService.findById(1L);

        // then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);

    }

    @Test
    void should_throw_exception_when_client_vendor_not_found() {
        // when
        // it throws exception since no mock of clientVendorRepository and clientVendorRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> clientVendorService.findById(0L));
        // then
        assertInstanceOf(ClientVendorNotFoundException.class, throwable);
        assertEquals("No Client or Vendor founded 0" , throwable.getMessage());
    }

    @Test
    void should_find_all_client_vendors() {
        // given
        List<ClientVendorDto> dtos = getDtos();

        List<ClientVendor> expectedList = getEntities();

        // when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(any()));
        when(clientVendorRepository.findAllByCompanyTitleAndSort("Test_Company")).thenReturn(expectedList);
        List<ClientVendorDto> actualList = clientVendorService.findAll();

        // then
        assertThat(actualList).usingRecursiveComparison()
//                .ignoringFields("hasInvoice")
                .isEqualTo(dtos);
    }

    @Test
    void should_find_all_client_vendors_by_type() {
        // given
        List<ClientVendorDto> dtos = getDtos();

        List<ClientVendor> expectedList = getEntities();

        // when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        when(clientVendorRepository.findAllByTypeAndSort("Test_Company", ClientVendorType.CLIENT)).thenReturn(expectedList);
        List<ClientVendorDto> actualList = clientVendorService.findAllByType(ClientVendorType.CLIENT);

        // then
        assertThat(actualList).usingRecursiveComparison()
                .ignoringFields("hasInvoice")
                .isEqualTo(dtos);
    }

    @Test
    void should_save_client_vendor() {
        // given
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        dto.setId(1L);
        dto.setCompany(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());

        // when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        when(clientVendorRepository.save(any(ClientVendor.class))).thenReturn(clientVendor);

        ClientVendorDto actualDto = clientVendorService.save(dto);

        // then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void should_update_client_vendor() {
        // given
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        dto.setId(1L);
        dto.setClientVendorName("UpdatingClient");
        dto.setCompany(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());

        // when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        when(clientVendorRepository.save(any(ClientVendor.class))).thenReturn(clientVendor);

        ClientVendorDto actualDto = clientVendorService.save(dto);

        // then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void should_not_update_and_throw_exception_when_client_vendor_not_found() {
        // when
        // it throws exception since no mock of clientVendorRepository and clientVendorRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> clientVendorService.update(TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT)));
        // then
        assertInstanceOf(ClientVendorNotFoundException.class, throwable);
        assertEquals("No Client or Vendor founded" , throwable.getMessage());
    }

    @Test
    void delete() {
        // given
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        dto.setId(1L);
        dto.setClientVendorName("DeletingClient");
        dto.setCompany(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());
        clientVendor.setIsDeleted(false);

        // when
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));
        when(clientVendorRepository.save(any(ClientVendor.class))).thenReturn(clientVendor);

        Throwable throwable= catchThrowable( () -> clientVendorService.delete(dto));

        // then
        assertTrue(clientVendor.getIsDeleted());
        assertNotEquals(dto.getClientVendorName(), clientVendor.getClientVendorName());
        assertNull(throwable);
    }

    @ParameterizedTest
    @MethodSource(value = "client")
    void isClientVendorExist(ClientVendorDto dto, ClientVendor clientVendor, boolean expected) {
        // when
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        when(clientVendorRepository.findByClientVendorName_AndCompany_Title(anyString(), anyString()))
                .thenReturn(Optional.ofNullable(clientVendor));
        // then
        assertEquals(expected, clientVendorService.isClientVendorExist(dto));
    }

    static Stream<Arguments> client(){
        // given
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        dto.setId(1L);
        ClientVendor clientVendor = new MapperUtil(new ModelMapper()).convert(dto, new ClientVendor());
        clientVendor.setId(2L);
        return Stream.of(
          arguments(dto, clientVendor, true),
          arguments(dto, null, false)
        );

    }

    private List<ClientVendorDto> getDtos(){
        List<ClientVendorDto> dtos = Arrays.asList(
                TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT),
                TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT),
                TestDocumentInitializer.getClientVendor(ClientVendorType.VENDOR)
        );
        dtos.get(0).setHasInvoice(true);
        dtos.get(1).setHasInvoice(true);
        dtos.get(2).setHasInvoice(true);
        return dtos;
    }

    private List<ClientVendor> getEntities(){
        List<ClientVendor> expectedList = getDtos().stream()
                .map(dto -> mapperUtil.convert(dto, new ClientVendor()))
                .collect(Collectors.toList());
        expectedList.get(0).setInvoices(Arrays.asList(TestDocumentInitializer.getInvoiceEntity(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE)));
        expectedList.get(1).setInvoices(Arrays.asList(TestDocumentInitializer.getInvoiceEntity(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE)));
        expectedList.get(2).setInvoices(Arrays.asList(TestDocumentInitializer.getInvoiceEntity(InvoiceStatus.AWAITING_APPROVAL, InvoiceType.PURCHASE)));
        return expectedList;
    }
}