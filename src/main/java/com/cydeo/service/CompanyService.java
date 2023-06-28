package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

    CompanyDto findById(long id);

    List<CompanyDto> listAllCompanies();

    void activateCompanyById(Long id);

    void deactivateCompanyById(Long id);

    CompanyDto save(CompanyDto companyDto);
}
