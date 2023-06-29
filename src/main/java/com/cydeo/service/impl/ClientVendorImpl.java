package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClientVendorImpl implements ClientVendorService {
    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;

    public ClientVendorImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public ClientVendorDto findById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findAll() {
        List<ClientVendor> listOfCV = clientVendorRepository.findAll();
        return listOfCV.stream().map(CV -> mapperUtil.convert(CV,new ClientVendorDto())).collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto save(ClientVendorDto clientVendorDto) {
        ClientVendor convertedCV = mapperUtil.convert(clientVendorDto, new ClientVendor());
        clientVendorRepository.save(convertedCV);
        return mapperUtil.convert(convertedCV, new ClientVendorDto());
    }

    @Override
    public ClientVendorDto update(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendorInDB = clientVendorRepository.findById(clientVendorDto.getId()).orElseThrow();
        ClientVendor convertedCV = mapperUtil.convert(clientVendorDto, new ClientVendor());

        convertedCV.setId(clientVendorInDB.getId());

        clientVendorRepository.save(convertedCV);
        return findById(clientVendorDto.getId());
    }

    @Override
    public ClientVendorDto delete(ClientVendorDto clientVendorDto) {
        ClientVendor clientVendor = clientVendorRepository.findByIdAndIsDeleted(clientVendorDto.getId(), false);
        clientVendor.setIsDeleted(true);
        clientVendor.setClientVendorName(clientVendorDto.getClientVendorName() + " - " + clientVendorDto.getId());
        clientVendorRepository.save(clientVendor);
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }
}
