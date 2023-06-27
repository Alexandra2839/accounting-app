package com.cydeo.dto;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {


    private Long id;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstname;
    private String lastname;
    private String phone;
    private RoleDto role;

    private CompanyDto company;

    private boolean isOnlyAdmin;
}
