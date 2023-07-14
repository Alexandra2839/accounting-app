package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.impl.CompanyServiceImpl;
import com.cydeo.service.impl.SecurityServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    static MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Test
    @DisplayName("findById_success")
    void findById_shouldReturnCompanyDto_whenCompanyExists() {
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
    void findById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist() {
        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable(() -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system", throwable.getMessage());
    }

    @Test
    @DisplayName("listAllCompanies_success")
    void listAllCompanies_shouldReturnListOfCompanyDtos() {
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
    void activateCompanyById_shouldActivateCompany_whenCompanyExists() {
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
    void activateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist() {

        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable(() -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system", throwable.getMessage());
    }

    @Test
    @DisplayName("deactivateCompany_success")
    void deactivateCompanyById_shouldActivateCompany_whenCompanyExists() {
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
    void deactivateCompanyById_shouldThrowCompanyNotFoundException_whenCompanyDoesNotExist() {
        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable(() -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system", throwable.getMessage());
    }

    @Test
    @DisplayName("saveCompany_success")
    void save_shouldSaveCompany_whenCompanyIsValid() {
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
    @DisplayName("updateById_success")
    void updateById_shouldUpdateCompany_whenCompanyExists() {
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
    @DisplayName("updateById_exception")
    void updateById_shouldThrowECompanyNotFoundException_whenCompanyDoesNotExist() {
        // it throws exception since no mock of company Repository and companyRepository.findById(1L) returns null
        Throwable throwable = catchThrowable(() -> companyServiceImpl.findById(0L));
        // then
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("Company with id 0 does not exist in the system", throwable.getMessage());
    }

    @Test
    @DisplayName("getCompanyDtoByLoggedInUser_success")
    void getCompanyDtoByLoggedInUser_shouldReturnCompanyDto_whenLoggedInUserHasCompany() {
        // Given
        Long id = 1L;
        CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        companyDto.setId(id);
        Company company = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        company.setId(companyDto.getId());
        UserDto loggedInUser = TestDocumentInitializer.getUser("role");

        when(securityServiceImpl.getLoggedInUser()).thenReturn(loggedInUser);
        // When
        CompanyDto companyDtoByUser = companyServiceImpl.getCompanyDtoByLoggedInUser();

        // Then
        assertEquals(companyDto.getId(), company.getId());
        assertEquals(companyDto.getTitle(), companyDtoByUser.getTitle());
        assertEquals(companyDto.getCompanyStatus(), CompanyStatus.PASSIVE);
    }

    @ParameterizedTest
    @DisplayName("isTitleExist")
    @MethodSource(value = "company")
    void isTitleExist(CompanyDto dto, Company company, boolean expected) {
        // when
        when(companyRepository.findByTitle(anyString())).thenReturn(Optional.ofNullable(company));
        // then
        assertEquals(expected, companyServiceImpl.isTitleExist(dto));
    }

    static Stream<Arguments> company(){
        // given
        CompanyDto dto = TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE);
        dto.setId(1L);
        Company company1 = new MapperUtil(new ModelMapper()).convert(dto, new Company());
        company1.setId(2L);
        return Stream.of(
                arguments(dto, company1, true),
                arguments(dto, null, false)
        );

    }

    @Test
    @DisplayName("listAllCompaniesByLoggedInUser_NotRoot_success")
    void listAllCompaniesByLoggedInUser_shouldReturnUsersCompanyDtos_whenLoggedInUserIsNotRootUser() {
        // Given
        UserDto userDto = TestDocumentInitializer.getUser("Admin");
        Company company1 = TestDocumentInitializer.getCompanyEntity(CompanyStatus.PASSIVE);
        List<CompanyDto> expectedList = List.of(mapperUtil.convert(company1, new CompanyDto()));

        // when
        when(securityServiceImpl.getLoggedInUser()).thenReturn(userDto);
        when(companyRepository.findByTitle(anyString())).thenReturn(Optional.ofNullable(company1));
        List<CompanyDto> actualList = companyServiceImpl.listAllCompaniesByLoggedInUser();

        //then
        assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);

    }

    @Test
    @DisplayName("listAllCompaniesByLoggedInUser_Root_success")
    void listAllCompaniesByLoggedInUser_shouldReturnAllCompanies_whenLoggedInUserIsRootUser() {
        // Given
        UserDto userDto = TestDocumentInitializer.getUser("Root User");
        List<CompanyDto> dtoList = getDtos();

        List<Company> companyList = getEntities();

        // when
        when(securityServiceImpl.getLoggedInUser()).thenReturn(userDto);
        when(companyRepository.findAllBesidesId1OrderedByStatusAndTitle()).thenReturn(companyList);
        List<CompanyDto> actualList = companyServiceImpl.listAllCompaniesByLoggedInUser();

        //then
        assertThat(actualList).usingRecursiveComparison().isEqualTo(dtoList);
    }

    private List<CompanyDto> getDtos() {
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












