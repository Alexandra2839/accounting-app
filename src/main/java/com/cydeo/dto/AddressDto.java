package com.cydeo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {


    private Long id;

    @NotBlank(message = "Address is a required field.")
    @Size(max = 100, min = 2, message = "Address should be 2-100 characters long.")
    private String addressLine1;

    @Size(max = 100, message = "Address should have maximum 100 characters long.")
    private String addressLine2;

    @NotBlank(message = "City is a required field.")
    @Size(max = 50, min = 2, message = "City should have 2-50 characters long.")
    private String city;

    @NotBlank(message = "State is a required field.")
    @Size(max = 50, min = 2, message = "State should have 2-50 characters long.")
    private String state;

    @NotNull(message = "Please select a country")
    private String country;

    @NotBlank(message = "Zip Code is a required field.")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Please put in proper format example: (*****-****)")
    private String zipCode;
}
