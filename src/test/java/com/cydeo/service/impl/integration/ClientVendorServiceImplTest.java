package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.exception.ClientVendorNotFoundException;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.impl.ClientVendorServiceImpl;
import com.cydeo.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClientVendorServiceImplTest {

    @Autowired
    ClientVendorServiceImpl clientVendorService;

    @Autowired
    ClientVendorRepository clientVendorRepository;

    @Autowired
    CompanyServiceImpl companyService;

//    @Spy
//    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Test
    @Transactional
    void should_find_clientVendor_by_id() {
        // when
        ClientVendorDto dto = clientVendorService.findById(1L);
        // then
        assertNotNull(dto);
        assertEquals("Orange Tech", dto.getClientVendorName());
    }

    @Test
    void should_throw_exception_when_client_vendor_not_found() {
        // when
        Throwable throwable = catchThrowable( () -> clientVendorService.findById(0L));
        // then
        assertInstanceOf(ClientVendorNotFoundException.class, throwable);
        assertEquals("No Client or Vendor founded 0" , throwable.getMessage());
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_find_all_client_vendors() {
        List<ClientVendorDto> dtos = clientVendorService.findAll();
        List<String> expectedTitles = List.of("Key Tech", "Reallinks Tech", "Livetube Tech", "Mod Tech");
        List<String> actualTitles = dtos.stream().map(ClientVendorDto::getClientVendorName).collect(Collectors.toList());
        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_find_all_client_vendors_by_type() {
        List<ClientVendorDto> dtos = clientVendorService.findAllByType(ClientVendorType.CLIENT);
        List<String> expectedTitles = List.of("Key Tech", "Reallinks Tech");
        List<String> actualTitles = dtos.stream().map(ClientVendorDto::getClientVendorName).collect(Collectors.toList());
        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_save_client_vendor() {
        ClientVendorDto dto = TestDocumentInitializer.getClientVendor(ClientVendorType.CLIENT);
        ClientVendorDto actualDto = clientVendorService.save(dto);
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFields("id", "company", "address.id")
                .isEqualTo(dto);
        assertNotNull(actualDto.getId());
        assertNotNull(actualDto.getAddress().getId());
        assertNotNull(actualDto.getCompany());
    }

    @Test
    @Transactional
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_update_client_vendor() {
        ClientVendorDto dto = clientVendorService.findById(1L);
        dto.setClientVendorName("Updating");
        ClientVendorDto actualDto = clientVendorService.update(dto);
        assertThat(actualDto).usingRecursiveComparison()
                .isEqualTo(dto);
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

    }
}