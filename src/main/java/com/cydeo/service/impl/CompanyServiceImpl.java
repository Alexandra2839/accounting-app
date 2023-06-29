package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrincipal;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;


    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;

    }


    @Override
    public CompanyDto findById(long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow( () -> new NoSuchElementException("Company with id " + id + " does not exist in the system"));
        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public List<CompanyDto> listAllCompanies() {

        List<Company> companyList = companyRepository.findAll();

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

        Company company = companyRepository.findById(id).orElseThrow(()->new RuntimeException("Company cannot be found"));
        Company convertedCompany = mapperUtil.convert(dto, new Company());
        convertedCompany.setId(company.getId());
        convertedCompany.setCompanyStatus(company.getCompanyStatus());
        companyRepository.save(convertedCompany);

        return mapperUtil.convert(convertedCompany, new CompanyDto());


    }
}
