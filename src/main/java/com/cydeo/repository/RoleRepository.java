package com.cydeo.repository;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
