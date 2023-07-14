package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@SpringBootTest
public class CompanyServiceImplIntegrationTest {

    @Autowired
    CompanyServiceImpl companyService;

    @Autowired
    CompanyRepository companyRepository;

    @Test
    @Transactional
    void should_find_company_by_id(){
        //when
        CompanyDto dto = companyService.findById(1L);
        //then
        assertNotNull(dto);
        assertEquals("CYDEO", dto.getTitle());
    }

    @Test
    void should_throw_exception_when_company_not_found() {

        // when
        Throwable throwable = catchThrowable( () -> companyService.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system" , throwable.getMessage());
    }

    @Test
    @Transactional
    @WithMockUser(username = "root@cydeo.com", password = "Abc1", roles = "Root User")
    void should_find_all_companies() {
        List<CompanyDto> dtos = companyService.listAllCompanies();
        List<String> expectedTitles = List.of("Blue Tech", "Green Tech", "Active Tech", "Orange Tech", "Red Tech");
        List<String> actualTitles = dtos.stream().map(CompanyDto::getTitle).collect(Collectors.toList());
        assertThat(expectedTitles).isEqualTo(actualTitles);
    }

    @Test
    @Transactional
    @WithMockUser(username = "root@cydeo.com", password = "Abc1", roles = "Root User")
    void should_save_company(){
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        CompanyDto actualDto = companyService.save(dto);
        assertThat(actualDto).usingRecursiveComparison().ignoringFields("id", "address.id")
                .isEqualTo(dto);
        assertNotNull(actualDto.getId());
    }

    @Test
    @Transactional
    @WithMockUser(username = "root@cydeo.com", password = "Abc1", roles = "Root User")
    void should_update_company(){
        CompanyDto dto = companyService.findById(1L);
        dto.setTitle("Updating");

        CompanyDto actualDto = companyService.updateById(dto.getId(), dto);

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void should_not_update_and_throw_exception_when_company_not_found() {
        // given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(100L);
        // when
        Throwable throwable = catchThrowable( () -> companyService.updateById(dto.getId(), dto));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 100 does not exist in the system" , throwable.getMessage());
    }

    @Test
    @Transactional
    void activate(){

        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(5L);
        companyService.activateCompanyById(dto.getId());
        Company company = companyRepository.findById(5L)
                .orElseThrow(()-> new NoSuchElementException("Company not found"));
        assertThat(company.getCompanyStatus().equals(CompanyStatus.ACTIVE));
    }

    @Test
    @Transactional
    void deactivate(){

        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE);
        dto.setId(2L);
        companyService.deactivateCompanyById(dto.getId());
        Company company = companyRepository.findById(2L)
                .orElseThrow(()-> new NoSuchElementException("Company not found"));
        assertThat(company.getCompanyStatus().equals(CompanyStatus.PASSIVE));
    }
















}



