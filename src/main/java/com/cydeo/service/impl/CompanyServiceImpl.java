package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    }

    @Override
    public void deactivateCompanyById(Long id) {

        Optional<Company> company = companyRepository.findById(id);

        if (company.isPresent()){
            company.get().setCompanyStatus(CompanyStatus.PASSIVE);
            companyRepository.save(company.get());
        }

    }
}
