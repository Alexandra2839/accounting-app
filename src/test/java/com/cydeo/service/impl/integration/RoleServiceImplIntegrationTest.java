package com.cydeo.service.impl.integration;

import com.cydeo.dto.RoleDto;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.impl.RoleServiceImpl;
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
public class RoleServiceImplIntegrationTest {
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    RoleRepository roleRepository;

    @Test
    @Transactional
    void should_find_role_by_id() {
        RoleDto roleDto = roleService.findById(1L);

        assertNotNull(roleDto);
        assertEquals("Root User", roleDto.getDescription());
    }

    @Test
    void should_throw_exception_when_role_id_not_found() {
        Throwable throwable = catchThrowable(() -> roleService.findById(0L));

        assertInstanceOf(RoleNotFoundException.class, throwable);
        assertEquals("Role with id " + "0" + " could not be found", throwable.getMessage());
    }

    @Test
    @Transactional
    @WithMockUser(username = "root@cydeo.com", password = "Abc1", roles = "Root User")
    void should_find_all_users_for_root_user() {
        List<RoleDto> dtos = roleService.listAllRoles();
        List<String> expectedDescription = List.of("Admin");
        List<String> actualDescription = dtos.stream().map(RoleDto::getDescription).collect(Collectors.toList());
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@greentech.com", password = "Abc1", roles = "Admin")
    void should_find_all_users_for_admin() {
        List<RoleDto> dtos = roleService.listAllRoles();
        List<String> expectedDescription = List.of("Admin", "Manager", "Employee");
        List<String> actualDescription = dtos.stream().map(RoleDto::getDescription).collect(Collectors.toList());
        assertEquals(expectedDescription, actualDescription);
        assertThat(actualDescription.size()).isEqualTo(3);
    }


}
