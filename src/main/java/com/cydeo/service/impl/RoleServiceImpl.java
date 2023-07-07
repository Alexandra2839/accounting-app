package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final MapperUtil mapperUtil;
    private final RoleRepository roleRepository;
    private final SecurityService securityService;


    public RoleServiceImpl(MapperUtil mapperUtil, RoleRepository roleRepository, SecurityService securityService) {
        this.mapperUtil = mapperUtil;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role with id " + id + " could not be found"));
        return mapperUtil.convert(role, new RoleDto());
    }

    @Override
    public List<RoleDto> listAllRoles() {

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return roleRepository.findAllByDescription("Admin").stream()
                    .map(role -> mapperUtil.convert(role, new RoleDto()))
                    .collect(Collectors.toList());
        }
        if (!securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return roleRepository.findAllByDescriptionNot("Root User").stream()
                    .map(role -> mapperUtil.convert(role, new RoleDto()))
                    .collect(Collectors.toList());
        }


        return roleRepository.findAll().stream()
                .map(role -> mapperUtil.convert(role, new RoleDto()))
                .collect(Collectors.toList());
    }
}
