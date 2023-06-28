package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
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
    private final UserPrincipal userPrincipal;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, UserPrincipal userPrincipal) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
        this.userPrincipal = userPrincipal;
    }


    @Override
    public CompanyDto findById(long id) {

        Optional<Company> company = companyRepository.findById(id);

        if(company.isPresent()){
            return mapperUtil.convert(company.get(), new CompanyDto());
        }

        else throw new NoSuchElementException("Company with id " + id + " does not exist in the system");

    }

    @Override
    public List<CompanyDto> listAllCompanies() {

        List<Company> companyList = companyRepository.findAll();

        return  companyList.stream().map(company -> mapperUtil.convert(company, new CompanyDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void activateCompanyById(Long id) {

        Optional<Company> company = companyRepository.findById(id);

        if (company.isPresent()){
            company.get().setCompanyStatus(CompanyStatus.ACTIVE);
            companyRepository.save(company.get());
        }
        else throw new NoSuchElementException("Company with id " + id + " does not exist in the system");

    }

    @Override
    public void deactivateCompanyById(Long id) {

        Optional<Company> company = companyRepository.findById(id);

        if (company.isPresent()){
            company.get().setCompanyStatus(CompanyStatus.PASSIVE);
            companyRepository.save(company.get());
        }
        else throw new NoSuchElementException("Company with id " + id + " does not exist in the system");

    }

    @Override
    public CompanyDto save(CompanyDto companyDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        Company company = mapperUtil.convert(companyDto, new Company());
        company.setInsertDateTime(LocalDateTime.now());
        companyRepository.save(company);

        return mapperUtil.convert(company, new CompanyDto());
    }
}
