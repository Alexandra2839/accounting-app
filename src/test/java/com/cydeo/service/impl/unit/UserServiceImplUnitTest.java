package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Spy
    static MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void should_throw_exception_when_user_does_not_exist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() -> userService.findById(1L));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
        assertEquals("User with id " + "1" + " could not be found", throwable.getMessage());

    }

    @Test
    void should_return_user_by_id_if_exists() {
        UserDto userDto = TestDocumentInitializer.getUser("admin");
        User user = mapperUtil.convert(userDto, new User());

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.findById(1L);
        assertThat(actualUser).usingRecursiveComparison().ignoringFields("password", "confirmPassword", "company.id").isEqualTo(userDto);

    }

    @Test
    void should_update_user_and_return_updated_user() {
        UserDto userDto = TestDocumentInitializer.getUser("role");
        userDto.setUsername("JohnSmith@test.com");
        userDto.setFirstname("John");
        User user = mapperUtil.convert(userDto, new User());

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);


        UserDto updatedUser = userService.update(userDto);
        assertThat(updatedUser.getUsername()).isEqualTo("JohnSmith@test.com");
    }

    @Test
    void should_return_user_list_for_root_user() {
        User user = TestDocumentInitializer.getUserEntity("Admin");
        User user2 = TestDocumentInitializer.getUserEntity("Admin");
        UserDto userDto = TestDocumentInitializer.getUser("Root User");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(userRepository.countByCompanyTitleAndRoleDescription(anyString(), anyString())).willReturn(2);
        given(userRepository.findAllByRoleDescriptionOrderByCompanyTitleAscRoleDescriptionAsc(anyString())).willReturn(List.of(user, user2));

        List<UserDto> userList = userService.listAllUsers();

        assertThat(userList).hasSize(2);
    }

    @Test
    void should_return_user_list_for_admin() {
        User user = TestDocumentInitializer.getUserEntity("Manager");
        UserDto userDto = TestDocumentInitializer.getUser("Admin");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(userRepository.countByCompanyTitleAndRoleDescription(anyString(), anyString())).willReturn(2);
        given(userRepository.findAllByCompanyTitleOrderByCompanyTitleAscRoleDescriptionAsc(anyString())).willReturn(List.of(user));

        List<UserDto> userList = userService.listAllUsers();

        assertThat(userList).hasSize(1);
    }

    @Test
    void should_save_user_and_return_saved_user() {
        User user = TestDocumentInitializer.getUserEntity("Manager");
        UserDto userDto = TestDocumentInitializer.getUser("Manager");

        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto savedUser = userService.save(userDto);

        assertThat(savedUser).isNotNull();
    }

    @Test
    void should_return_false_if_email_does_not_exist() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(null);

        assertThat(userService.isEmailExist(userDto)).isFalse();
    }

    @Test
    void should_return_true_if_email_exist() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");
        User user = TestDocumentInitializer.getUserEntity("Admin");
        user.setId(2L);

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(user);

        assertThat(userService.isEmailExist(userDto)).isTrue();
    }

    @Test
    void should_throw_exception_when_user_id_to_delete_does_not_exist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.delete(1L));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void should_soft_delete_user() {
        User user = TestDocumentInitializer.getUserEntity("Manager");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository, times(1)).save(user);
        assertThat(user.getIsDeleted()).isTrue();
    }

    @Test
    void should_set_only_admin_if_condition_met() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");

        when(userRepository.countByCompanyTitleAndRoleDescription(anyString(), anyString())).thenReturn(1);

        UserDto updatedUser = userService.setOnlyAdmin(userDto);
        assertThat(updatedUser.isOnlyAdmin()).isTrue();
    }


}