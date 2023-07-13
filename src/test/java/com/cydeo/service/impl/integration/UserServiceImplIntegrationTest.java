package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void should_return_user_by_username() {
        UserDto userDto = userService.findByUsername("admin@greentech.com");

        assertNotNull(userDto);
    }

    @Test
    public void should_throw_exception_when_id_does_not_exist() {
        Long id = 22L;
        Throwable throwable = catchThrowable(() -> userService.findById(id));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
        assertThat(throwable).hasMessage("User with id " + id + " could not be found");
    }

    @Test
    @Transactional
    public void should_return_user_given_id() {
        UserDto userDto = userService.findById(1L);

        assertNotNull(userDto);
        assertEquals("Martin", userDto.getLastname());

    }

    @Test
    @Transactional
    @WithMockUser(username = "root@cydeo.com", password = "Abc1", roles = "Root User")
    public void should_return_all_admins_when_logged_in_as_root_user() {
        List<UserDto> userDtos = userService.listAllUsers();
        List<String> expected = List.of("Chris", "Mary", "Garrison", "John");
        List<String> actual = userDtos.stream().map(UserDto::getFirstname).collect(Collectors.toList());
        assertThat(expected).isEqualTo(actual);
        assertEquals(actual.size(), 4);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@bluetech.com", password = "Abc1", roles = "Admin")
    public void should_return_all_users_of_logged_in_admin() {
        List<UserDto> userDtos = userService.listAllUsers();
        List<String> expected = List.of("Chris", "Mike", "Tom");
        List<String> actual = userDtos.stream().map(UserDto::getFirstname).collect(Collectors.toList());
        assertThat(expected).isEqualTo(actual);
        assertEquals(actual.size(), 3);
    }

    @Test
    @Transactional
    public void should_save_user() {
        UserDto userDto = TestDocumentInitializer.getUser("Manager");
        UserDto savedDto = userService.save(userDto);

        assertNotNull(savedDto.getId());
        assertThat(savedDto).usingRecursiveComparison()
                .ignoringFields("id", "company.id", "role.id", "password", "confirmPassword")
                .isEqualTo(userDto);

        User user = userRepository.findById(savedDto.getId()).orElseThrow();
        userRepository.delete(user);

    }

    @Test
    @Transactional
    public void should_update_user() {
        UserDto userDto = userService.findById(1L);
        userDto.setFirstname("Updated Name");

        UserDto actualUserDto = userService.update(userDto);

        assertThat(actualUserDto).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    @Transactional
    public void should_delete_user() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");
        userDto.setId(9L);

        userService.delete(userDto.getId());

        User user = userRepository.findById(9L).orElseThrow(() -> new UserNotFoundException("User not found"));

        assertTrue(user.getIsDeleted());

        user.setIsDeleted(false);
        userRepository.save(user);
    }

    @Test
    @Transactional
    public void should_set_only_admin() {
        UserDto dto = userService.findById(6L);
        UserDto userDto = userService.setOnlyAdmin(dto);

        assertTrue(userDto.isOnlyAdmin());
    }


}
