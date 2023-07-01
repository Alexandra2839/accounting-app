package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, PasswordEncoder passwordEncoder, @Lazy SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public UserDto findByUsername(String username) {
        return mapperUtil.convert(userRepository.findByUsername(username), new UserDto());
    }

    @Override
    public UserDto findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + "could not be found"));
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> listAllUsers() {

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return userRepository.findAllByRoleDescription("Admin").stream()
                    .map(user -> mapperUtil.convert(user, new UserDto()))
                    .collect(Collectors.toList());
        }

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Admin")) {
            return userRepository.findAllByCompanyTitle(securityService.getLoggedInUser().getCompany().getTitle())
                    .stream()
                    .map(user -> mapperUtil.convert(user, new UserDto()))
                    .collect(Collectors.toList());
        }

        return userRepository.findAllByOrderByCompanyTitleAscRoleDescriptionAsc().stream()
                .map(user -> mapperUtil.convert(user, new UserDto()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto user) {


        User user1 = userRepository.findByUsername(user.getUsername());

        User convertedUser = mapperUtil.convert(user, new User());   // has id?

        convertedUser.setId(user1.getId());

        userRepository.save(convertedUser);

        return findByUsername(user.getUsername());

    }

    @Override
    public UserDto save(UserDto user) {
        user.setEnabled(true);

        User obj = mapperUtil.convert(user, new User());
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));

        userRepository.save(obj);
        return mapperUtil.convert(obj, new UserDto());
    }

    @Override
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " could not be found"));

        user.setIsDeleted(true);
        user.setUsername(user.getUsername() + "-" + user.getId());
        userRepository.save(user);

    }

    @Override
    public boolean isEmailExist(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());

        if (user == null)
            return false;

        return !Objects.equals(userDto.getId(), user.getId());
    }

    @Override
    public void setOnlyAdmin(UserDto userDto) {

        if (userRepository.countByCompanyTitleAndRoleDescription(userDto.getCompany().getTitle(), userDto.getRole().getDescription()) == 1)
            userDto.setOnlyAdmin(true);

    }
}
