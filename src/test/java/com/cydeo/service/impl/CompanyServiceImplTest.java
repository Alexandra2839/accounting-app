package com.cydeo.service.impl;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @InjectMocks
    CompanyServiceImpl companyServiceImpl;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    SecurityServiceImpl securityServiceImpl;

    @Spy
    static MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    Company company;
    CompanyDto companyDto;

    @BeforeEach
    void setUp(){
        company = new Company();
        company.setId(1L);
        company.setTitle("Test company");
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        company.setPhone("000-000-0000");
        company.setWebsite("https://www.test.com");
    }

    @Test
    @DisplayName("findById_success")
    public void findById_shouldReturnCompanyDto_whenCompanyExists() {
        //Given
        Long id = 1L;
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(id);
        //When
        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        //Then
        assertThat(companyServiceImpl.findById(id)).isEqualTo(mapperUtil.convert(company, new CompanyDto()));
    }

    @Test
    @DisplayName("findById_exception")
    public void findById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist() {
        // When
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());
        // Then
        assertThrows(CompanyNotFoundException.class, () -> companyServiceImpl.findById(1L));
    }

    @Test
    @DisplayName("listAllCompanies_success")
    public void listAllCompanies_shouldReturnListOfCompanyDtos() {
        // Given
        List<Company> companies = List.of(
                new Company(),
                new Company()
        );

        //When
        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle()).thenReturn(companies);
        when(mapperUtil.convert(companies.get(0), new CompanyDto())).thenReturn(new CompanyDto());
        when(mapperUtil.convert(companies.get(1), new CompanyDto())).thenReturn(new CompanyDto());

        List<CompanyDto> companyDtos = companyServiceImpl.listAllCompanies();

        // Then
        assertEquals(2, companyDtos.size());
        assertEquals(companyDtos.get(0).getId(), companies.get(0).getId());
        assertEquals(companyDtos.get(1).getId(), companies.get(1).getId());
    }

    @Test
    @DisplayName("activateCompany_success")
    public void activateCompanyById_shouldActivateCompany_whenCompanyExists() {
        // Given
        Long id = 1L;
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(id);

        when(companyRepository.findById(id)).thenReturn(java.util.Optional.of(company));

        // When
        companyServiceImpl.activateCompanyById(id);

        // Then
        assertEquals(company.getCompanyStatus(), CompanyStatus.ACTIVE);
    }

    @Test
    @DisplayName("activateCompany_exception")
    public void activateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist(){

        when(companyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CompanyNotFoundException.class, () -> companyServiceImpl.findById(1L));

    }

    @Test
    @DisplayName("deactivateCompany_success")
    public void deactivateCompanyById_shouldActivateCompany_whenCompanyExists() {
        // Given
        Long id = 1L;
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.ACTIVE);
        company.setId(id);

        when(companyRepository.findById(id)).thenReturn(java.util.Optional.of(company));

        // When
        companyServiceImpl.deactivateCompanyById(id);

        // Then
        assertEquals(company.getCompanyStatus(), CompanyStatus.PASSIVE);
    }

    @Test
    @DisplayName("deactivateCompany_exception")
    public void deactivateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist(){

        when(companyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CompanyNotFoundException.class, () -> companyServiceImpl.findById(1L));

    }

    @Test
    @DisplayName("saveCompany_success")
    public void save_shouldSaveCompany_whenCompanyIsValid(){
        Long id = 1L;
        CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        companyDto.setId(id);
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(companyDto.getId());

        when(mapperUtil.convert(companyDto, new Company())).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(mapperUtil.convert(company, new CompanyDto())).thenReturn(companyDto);

        CompanyDto savedCompanyDto = companyServiceImpl.save(companyDto);

        assertThat(savedCompanyDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(companyDto);
    }

    @Test
    public void updateById_shouldUpdateCompany_whenCompanyExists() {
        // Given
        Long id = 1L;
        CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        companyDto.setTitle("New Company Title");

        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(id);
        company.setTitle("Company A");


        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(mapperUtil.convert(company, new CompanyDto())).thenReturn(companyDto);


        // When
        CompanyDto updatedCompanyDto = companyServiceImpl.updateById(id, companyDto);

        // Then
        assertEquals(updatedCompanyDto.getId(), id);
        assertEquals(updatedCompanyDto.getTitle(), "New Company Title");
        assertEquals(updatedCompanyDto.getCompanyStatus(), CompanyStatus.PASSIVE);
    }

    @Test
    public void getCompanyDtoByLoggedInUser_shouldReturnCompanyDto_whenLoggedInUserHasCompany() {
        // Given
        Long id = 1L;
        CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        companyDto.setId(id);
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(companyDto.getId());
        UserDto loggedInUser = TestDocumentInitializer.getUser("role");

        when(securityServiceImpl.getLoggedInUser()).thenReturn(loggedInUser);

        when(mapperUtil.convert(company, new CompanyDto())).thenReturn(new CompanyDto());

        // When
        CompanyDto companyDtoByUser = companyServiceImpl.getCompanyDtoByLoggedInUser();

        // Then
        assertEquals(companyDto.getId(), company.getId());
        assertEquals(companyDto.getTitle(), "Company A");
        assertEquals(companyDto.getCompanyStatus(), CompanyStatus.PASSIVE);
    }

    @Test
    public void getCompanyDtoByLoggedInUser_shouldThrowCompanyNotFoundException_whenLoggedInUserHasNoCompany() {
        // Given
        when(securityServiceImpl.getLoggedInUser().getCompany()).thenReturn(null);

        // When
        assertThrows(CompanyNotFoundException.class, () -> companyServiceImpl.getCompanyDtoByLoggedInUser());
    }

    @Test
    public void isTitleExist_shouldReturnFalse_whenCompanyWithTitleExists() {
        // Given
        CompanyDto companyDto = new CompanyDto();
        companyDto.setTitle("Company A");

        Company company = new Company();
        company.setTitle(companyDto.getTitle());

        when(companyRepository.findByTitle(companyDto.getTitle())).thenReturn(Optional.of(company));

        // When
        boolean isTitleExist = companyServiceImpl.isTitleExist(companyDto);

        // Then
        assertFalse(isTitleExist);
    }

    @Test
    public void isTitleExist_shouldReturnTrue_whenCompanyWithTitleDoesNotExist() {
        // Given
        CompanyDto companyDto = new CompanyDto();
        companyDto.setTitle("Company A");

        when(companyRepository.findByTitle(companyDto.getTitle())).thenReturn(Optional.empty());

        // When
        boolean isTitleExist = companyServiceImpl.isTitleExist(companyDto);

        // Then
        assertTrue(isTitleExist);
    }

    @Test
    public void listAllCompaniesByLoggedInUser_shouldReturnListOfCompanyDtos_whenLoggedInUserIsNotRootUser() {
        // Given
    }

    @Test
    public void listAllCompaniesByLoggedInUser_shouldReturnAllCompanies_whenLoggedInUserIsRootUser() {
        // Given
        when(securityServiceImpl.getLoggedInUser().getRole().getDescription()).thenReturn("Root User");

        List<Company> companies = List.of(
                new Company(),
                new Company()
        );

        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle()).thenReturn(companies);
        when(mapperUtil.convert(companies.get(0), new CompanyDto())).thenReturn(new CompanyDto());
        when(mapperUtil.convert(companies.get(1), new CompanyDto())).thenReturn(new CompanyDto());

        // When
        List<CompanyDto> companyDtos = companyServiceImpl.listAllCompaniesByLoggedInUser();

        // Then
        assertEquals(2, companyDtos.size());
    }
}












