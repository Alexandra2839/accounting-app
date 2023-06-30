package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;


    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;

        this.securityService = securityService;
    }


    @Override
    public CompanyDto findById(long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company with id " + id + " does not exist in the system"));
        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public List<CompanyDto> listAllCompanies() {

        List<Company> companyList = companyRepository.findAllBesidesId1OrderedByStatusAndTitle();

        return companyList.stream().map(company -> mapperUtil.convert(company, new CompanyDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void activateCompanyById(Long id) {

        Optional<Company> company = companyRepository.findById(id);

        if (company.isPresent()) {
            company.get().setCompanyStatus(CompanyStatus.ACTIVE);
            companyRepository.save(company.get());
        } else throw new NoSuchElementException("Company with id " + id + " does not exist in the system");

    }

    @Override
    public void deactivateCompanyById(Long id) {

        Optional<Company> company = companyRepository.findById(id);

        if (company.isPresent()) {
            company.get().setCompanyStatus(CompanyStatus.PASSIVE);
            companyRepository.save(company.get());
        } else throw new NoSuchElementException("Company with id " + id + " does not exist in the system");

    }

    @Override
    public CompanyDto save(CompanyDto companyDto) {


        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        Company company = mapperUtil.convert(companyDto, new Company());

        companyRepository.save(company);

        return mapperUtil.convert(company, new CompanyDto());
    }


    @Override
    public CompanyDto updateById(Long id, CompanyDto dto) {

        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company cannot be found"));
        Company convertedCompany = mapperUtil.convert(dto, new Company());
        convertedCompany.setId(company.getId());
        convertedCompany.setCompanyStatus(company.getCompanyStatus());
        companyRepository.save(convertedCompany);

        return mapperUtil.convert(convertedCompany, new CompanyDto());


    }

    @Override
    public CompanyDto getCompanyDtoByLoggedInUser() {

        return securityService.getLoggedInUser().getCompany();


    }

    @Override
    public boolean isTitleExist(CompanyDto companyDto) {

        Company company = companyRepository.findByTitle(companyDto.getTitle()).orElse(null);

        if (company == null) return false;

        return !Objects.equals(companyDto.getId(), company.getId());
    }

    @Override
    public List<CompanyDto> listAllCompaniesByLoggedInUser() {

        if (!securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return companyRepository.findByTitle(securityService.getLoggedInUser().getCompany().getTitle())
                    .stream()
                    .map(company -> mapperUtil.convert(company, new CompanyDto()))
                    .collect(Collectors.toList());
        }

        return listAllCompanies();
    }
}
