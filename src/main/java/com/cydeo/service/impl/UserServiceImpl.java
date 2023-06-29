package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
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
}
