package com.cydeo.dto;

import lombok.*;


import javax.validation.constraints.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {


    private Long id;
    @NotBlank(message = "Email is required field.")
    @Email
    private String username;
    @Pattern(regexp = "(?=.*[!@#$%^&*_0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}",
            message = "Password should be at least 4 characters long and needs to contain 1 capital letter, 1 small letter and 1 special character or number.")
    private String password;

    @NotNull(message = "Passwords should match.")
    private String confirmPassword;
    @NotBlank(message = "First Name is required field.")
    @Size(max = 50, min = 2, message = "First Name must be between 2 and 50 characters long.")
    private String firstname;
    @NotBlank(message = "Last Name is required field.")
    @Size(max = 50, min = 2, message = "Last Name must be between 2 and 50 characters long.")
    private String lastname;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$"
            , message = "Phone Number is required field and may be in any valid phone number format.")
    private String phone;

    @NotNull(message = "Please select a Role.")
    private RoleDto role;
    private boolean enabled;

    @NotNull(message = "Please select a Company.")
    private CompanyDto company;

    private boolean isOnlyAdmin;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    public boolean isOnlyAdmin() {
        return isOnlyAdmin;
    }

    public void setOnlyAdmin(boolean onlyAdmin) {
        isOnlyAdmin = onlyAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        checkConfirmPassword();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        checkConfirmPassword();
    }

    private void checkConfirmPassword() {
        if (this.password == null || this.confirmPassword == null) {
            return;
        } else if (!this.password.equals(this.confirmPassword)) {
            this.confirmPassword = null;
        }
    }

}
