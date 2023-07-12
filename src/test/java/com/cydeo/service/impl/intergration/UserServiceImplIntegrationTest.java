package com.cydeo.service.impl.intergration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
   // @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityService securityService;

    @Test
    public void should_throw_exception_when_id_does_not_exist(){
        Long id = 22L;
        Throwable throwable = catchThrowable(() -> userService.findById(id));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
        assertThat(throwable).hasMessage("User with id " + id + " could not be found");
    }

    @Test
    public void should_return_user_given_id(){

    }




}
