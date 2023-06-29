package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;


import java.util.List;

public interface ClientVendorService {
    ClientVendorDto findById(Long id);
    List<ClientVendorDto> findAll();

    ClientVendorDto save(ClientVendorDto clientVendorDto);
    ClientVendorDto update(ClientVendorDto clientVendorDto);
    ClientVendorDto delete(ClientVendorDto clientVendorDto);
}
