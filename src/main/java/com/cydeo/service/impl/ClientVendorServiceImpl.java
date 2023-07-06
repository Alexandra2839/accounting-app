package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {
    private final ClientVendorRepository clientVendorRepository;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, CompanyService companyService, MapperUtil mapperUtil) {
        this.clientVendorRepository = clientVendorRepository;
        this.companyService = companyService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public ClientVendorDto findById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Client or Vendor founded " + id));
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findAll() {
        List<ClientVendor> listOfCV = clientVendorRepository
                .findAllByCompanyTitleAndSort(companyService.getCompanyDtoByLoggedInUser().getTitle());
        return listOfCV.stream()
                .map(cv -> {
                    ClientVendorDto dto = mapperUtil.convert(cv, new ClientVendorDto());
                    dto.setHasInvoice(!cv.getInvoices().isEmpty());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> findAllByType(ClientVendorType type) {
        return clientVendorRepository.findAllByTypeAndSort(companyService.getCompanyDtoByLoggedInUser().getTitle(), type)
                .stream()
                .map(cv -> mapperUtil.convert(cv, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto save(ClientVendorDto clientVendorDto) {
        ClientVendor convertedCV = mapperUtil.convert(clientVendorDto, new ClientVendor());
        convertedCV.setCompany(mapperUtil.convert(companyService.getCompanyDtoByLoggedInUser(), new Company()));
        clientVendorRepository.save(convertedCV);
        return mapperUtil.convert(convertedCV, new ClientVendorDto());
    }

    @Override
    public ClientVendorDto update(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendorInDB = clientVendorRepository.findById(clientVendorDto.getId())
                .orElseThrow(() -> new NoSuchElementException("No Client or Vendor founded"));
        clientVendorDto.getAddress().setId(clientVendorInDB.getAddress().getId());     // otherwise it creates new address instead of updating existing one
        ClientVendor convertedCV = mapperUtil.convert(clientVendorDto, new ClientVendor());

        convertedCV.setId(clientVendorInDB.getId());
        convertedCV.setCompany(clientVendorInDB.getCompany());
        ClientVendor saved = clientVendorRepository.save(convertedCV);
        return mapperUtil.convert(saved, new ClientVendorDto());
    }

    @Override
    public void delete(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = clientVendorRepository.findById(clientVendorDto.getId())
                .orElseThrow(() -> new NoSuchElementException("No Client or Vendor founded"));
        clientVendor.setIsDeleted(true);
        clientVendor.setClientVendorName(clientVendorDto.getClientVendorName() + " - " + clientVendorDto.getId());
        clientVendorRepository.save(clientVendor);
    }

    @Override
    public boolean isClientVendorExist(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = clientVendorRepository.findByClientVendorName_AndCompany_Title(
                        clientVendorDto.getClientVendorName().trim(), companyService.getCompanyDtoByLoggedInUser().getTitle())
                .orElse(null);
        if (clientVendor == null) return false;

        return !Objects.equals(clientVendorDto.getId(), clientVendor.getId());
    }
}
