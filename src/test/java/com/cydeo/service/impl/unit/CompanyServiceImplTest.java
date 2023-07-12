package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Address;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.ClientVendorNotFoundException;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.impl.CompanyServiceImpl;
import com.cydeo.service.impl.SecurityServiceImpl;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
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
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Test
    @DisplayName("findById_success")
    public void findById_shouldReturnCompanyDto_whenCompanyExists() {
        //Given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(1L);
        Company company = mapperUtil.convert(dto, new Company());
        //When
        when(companyRepository.findById(dto.getId())).thenReturn(Optional.of(company));
        CompanyDto actualDto = companyServiceImpl.findById(1L);
        //Then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }


    @Test
    @DisplayName("findById_exception")
    public void findById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist() {
        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system" , throwable.getMessage());
    }

    @Test
    @DisplayName("listAllCompanies_success")
    public void listAllCompanies_shouldReturnListOfCompanyDtos() {
        // Given
        List<CompanyDto> dtos = getDtos();
        List<Company> expectedList = getEntities();
        //When
        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle()).thenReturn(expectedList);
        List<CompanyDto> actualList = companyServiceImpl.listAllCompanies();
        // then
        assertThat(actualList).usingRecursiveComparison()
                .isEqualTo(dtos);

    }
    @Test
    @DisplayName("activateCompany_success")
    public void activateCompanyById_shouldActivateCompany_whenCompanyExists() {
        // Given
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(java.util.Optional.of(company));

        // When
        companyServiceImpl.activateCompanyById(1L);

        // Then
        assertEquals(company.getCompanyStatus(), CompanyStatus.ACTIVE);
    }

    @Test
    @DisplayName("activateCompany_exception")
    public void activateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist(){

        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system" , throwable.getMessage());
    }

    @Test
    @DisplayName("deactivateCompany_success")
    public void deactivateCompanyById_shouldActivateCompany_whenCompanyExists() {
        // Given
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.ACTIVE);
        company.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(java.util.Optional.of(company));
        // When
        companyServiceImpl.deactivateCompanyById(1L);
        // Then
        assertEquals(company.getCompanyStatus(), CompanyStatus.PASSIVE);
    }

    @Test
    @DisplayName("deactivateCompany_exception")
    public void deactivateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist(){
// it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system" , throwable.getMessage());
    }

    @Test
    @DisplayName("saveCompany_success")
    public void save_shouldSaveCompany_whenCompanyIsValid(){

        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(1L);
        Company company = mapperUtil.convert(dto, new Company());
        //When
        when(companyRepository.save(ArgumentMatchers.any())).thenReturn(company);
        CompanyDto actualDto = companyServiceImpl.save(dto);
        //Then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    public void updateById_shouldUpdateCompany_whenCompanyExists() {
        // Given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setTitle("UpdatingCompany");
        Company company = mapperUtil.convert(dto, new Company());

        when(companyRepository.findById(dto.getId())).thenReturn(Optional.of(company));
        when(companyRepository.save(ArgumentMatchers.any())).thenReturn(company);

        // When
        CompanyDto actualDto = companyServiceImpl.updateById(dto.getId(), dto);

        // Then
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void updateById_shouldThrowECompanyNotFoundException_whenCompanyDoesNotExist() {
        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable( () -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system" , throwable.getMessage());
    }

    @Test
    public void getCompanyDtoByLoggedInUser_shouldReturnCompanyDto_whenLoggedInUserHasCompany() {
        // Given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(1L);
        Company company = mapperUtil.convert(dto, new Company());

        when(companyServiceImpl.getCompanyDtoByLoggedInUser()).thenReturn(dto);
        // When
        CompanyDto companyDtoByUser = securityServiceImpl.getLoggedInUser().getCompany();

        // Then
        assertThat(companyDtoByUser).usingRecursiveComparison().isEqualTo(dto);
    }
//
//    @Test
//    public void getCompanyDtoByLoggedInUser_shouldThrowCompanyNotFoundException_whenLoggedInUserHasNoCompany() {
//        // Given
//        when(securityServiceImpl.getLoggedInUser().getCompany()).thenReturn(null);
//
//        // When
//        assertThrows(CompanyNotFoundException.class, () -> companyServiceImpl.getCompanyDtoByLoggedInUser());
//    }
//
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

    @ParameterizedTest
    @MethodSource(value = "company")
    void isTitleExist(CompanyDto dto, Company company, boolean expected) {
        // when
//        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle())
//                .thenReturn(Optional.ofNullable(company));
        // then
        assertEquals(expected, companyServiceImpl.isTitleExist(dto));
    }

    static Stream<Arguments> company(){
        // given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(1L);
        Company company = new MapperUtil(new ModelMapper()).convert(dto, new Company());
        company.setId(2L);
        return Stream.of(
                arguments(dto, company, true),
                arguments(dto, null, false)
        );

    }

//    @Test
//    public void listAllCompaniesByLoggedInUser_shouldReturnListOfCompanyDtos_whenLoggedInUserIsNotRootUser() {
//        // Given
//    }
//
//    @Test
//    public void listAllCompaniesByLoggedInUser_shouldReturnAllCompanies_whenLoggedInUserIsRootUser() {
//        // Given
//        when(securityServiceImpl.getLoggedInUser().getRole().getDescription()).thenReturn("Root User");
//
//        List<Company> companies = List.of(
//                new Company(),
//                new Company()
//        );
//
//        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle()).thenReturn(companies);
//        when(mapperUtil.convert(companies.get(0), new CompanyDto())).thenReturn(new CompanyDto());
//        when(mapperUtil.convert(companies.get(1), new CompanyDto())).thenReturn(new CompanyDto());
//
//        // When
//        List<CompanyDto> companyDtos = companyServiceImpl.listAllCompaniesByLoggedInUser();
//
//        // Then
//        assertEquals(2, companyDtos.size());
//    }

    private List<CompanyDto> getDtos(){
        List<CompanyDto> dtos = Arrays.asList(
                TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE),
                TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE),
                TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE)
        );
        return dtos;
    }

    private List<Company> getEntities() {
        List<Company> expectedList = getDtos().stream()
                .map(dto -> mapperUtil.convert(dto, new Company())).collect(Collectors.toList());
        return expectedList;
    }











}












