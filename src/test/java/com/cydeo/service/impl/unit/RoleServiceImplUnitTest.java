package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplUnitTest {

    @InjectMocks
    RoleServiceImpl roleService;
    @Spy
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SecurityService securityService;

    @Test
    public void should_throw_exception_when_role_does_not_exist() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() -> roleService.findById(1L));
        assertThat(throwable).isInstanceOf(RoleNotFoundException.class);

    }

    @Test
    public void should_return_user_by_id_if_exists() {
        RoleDto roleDto = new RoleDto(1L, "Admin");
        Role role = mapperUtil.convert(roleDto, new Role());

        when(roleRepository.findById(roleDto.getId())).thenReturn(Optional.of(role));

        RoleDto actualUser = roleService.findById(1L);
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(roleDto);

    }

    @Test
    public void should_return_role_list_for_root_user() {
        Role role = new Role("Admin");
        UserDto userDto = TestDocumentInitializer.getUser("Root User");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(roleRepository.findAllByDescription("Admin")).willReturn(List.of(role));

        List<RoleDto> roleList = roleService.listAllRoles();

        assertThat(roleList).isNotNull();
        assertThat(roleList.size()).isEqualTo(1);
    }

    @Test
    public void should_return_role_list_for_not_root_user() {
        Role role = new Role("Admin");
        Role role2 = new Role("Manager");
        Role role3 = new Role("Employee");
        UserDto userDto = TestDocumentInitializer.getUser("Admin");

        given(securityService.getLoggedInUser()).willReturn(userDto);
        given(roleRepository.findAllByDescriptionNot("Root User")).willReturn(List.of(role, role2, role3));

        List<RoleDto> roleList = roleService.listAllRoles();

        assertThat(roleList).isNotNull();
        assertThat(roleList.size()).isEqualTo(3);
    }


}