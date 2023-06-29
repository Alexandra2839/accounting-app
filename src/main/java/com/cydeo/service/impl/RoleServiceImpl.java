package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final MapperUtil mapperUtil;
    private final RoleRepository roleRepository;


    public RoleServiceImpl(MapperUtil mapperUtil, RoleRepository roleRepository) {
        this.mapperUtil = mapperUtil;
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException ("Role with id "+ id + " could not be found"));
        return mapperUtil.convert(role, new RoleDto());
    }

    @Override
    public List<RoleDto> listAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> mapperUtil.convert(role,new RoleDto()))
                .collect(Collectors.toList());
    }
}
