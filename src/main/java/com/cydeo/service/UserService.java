package com.cydeo.service;

import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {


    UserDto findByUsername(String username);

    UserDto findById(Long id);

    List<UserDto> listAllUsers();

    UserDto update(UserDto user);

    UserDto save(UserDto user);
}
