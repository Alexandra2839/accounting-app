package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface ClientVendorService {
    ClientVendorDto findById(Long id);

    List<ClientVendorDto> findAll();

    List<ClientVendorDto> findAllByType(ClientVendorType type);

    ClientVendorDto save(ClientVendorDto clientVendorDto);

    ClientVendorDto update(ClientVendorDto clientVendorDto);

    void delete(ClientVendorDto clientVendorDto);
}
