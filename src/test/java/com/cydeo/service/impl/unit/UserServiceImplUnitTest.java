package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void should_trow_exception_when_user_does_not_exist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() -> userService.findById(1L));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);

    }

    @Test
    public void should_return_user_by_id_if_exists() {

        User user = TestDocumentInitializer.getUserEntity("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(userService.findById(1L)).isEqualTo(mapperUtil.convert(user, new UserDto()));

    }

    @Test
    public void should_update_user_and_return_updated_user() {
        User user = TestDocumentInitializer.getUserEntity("role");
        UserDto userDto = TestDocumentInitializer.getUser("role");
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(userDto);
        userDto.setUsername("JohnSmith@test.com");
        userDto.setFirstname("John");
        UserDto updatedUser = userService.update(userDto);
        assertThat(updatedUser.getUsername()).isEqualTo("JohnSmith@test.com");
    }

    @Test
    public void should_return_user_list_for_root_user() {
        User user = TestDocumentInitializer.getUserEntity("Admin");
        User user2 = TestDocumentInitializer.getUserEntity("Admin");
        UserDto userDto = TestDocumentInitializer.getUser("Root User");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(userRepository.countByCompanyTitleAndRoleDescription(anyString(), anyString())).willReturn(2);
        given(userRepository.findAllByRoleDescriptionOrderByCompanyTitleAscRoleDescriptionAsc(anyString())).willReturn(List.of(user, user2));
        given(mapperUtil.convert(any(User.class), any(UserDto.class))).willReturn(TestDocumentInitializer.getUser("Admin"));

        List<UserDto> userList = userService.listAllUsers();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    public void should_return_user_list_for_admin() {
        User user = TestDocumentInitializer.getUserEntity("Manager");
        UserDto userDto = TestDocumentInitializer.getUser("Admin");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(userRepository.countByCompanyTitleAndRoleDescription(anyString(), anyString())).willReturn(2);
        given(userRepository.findAllByCompanyTitleOrderByCompanyTitleAscRoleDescriptionAsc(anyString())).willReturn(List.of(user));
        given(mapperUtil.convert(any(User.class), any(UserDto.class))).willReturn(TestDocumentInitializer.getUser("Admin"));

        List<UserDto> userList = userService.listAllUsers();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(1);
    }

    @Test
    public void should_save_user_and_return_saved_user(){
        User user = TestDocumentInitializer.getUserEntity("Manager");
        UserDto userDto = TestDocumentInitializer.getUser("Manager");

        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(userDto);

        UserDto savedUser = userService.save(userDto);

        assertThat(savedUser).isNotNull();


    }


}