package com.cydeo.dto;


import com.cydeo.enums.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDto {

    private Long id;

    @NotBlank(message = "Title is a required field")
    @Size(max = 100, min = 2, message = "Title should be 2-100 characters long")
    private String title;

    @NotBlank(message = "Phone Number is a required field.")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" // +111 (202) 555-0125  +1 (202) 555-0125
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"                                  // +111 123 456 789
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$",
            message = "Phone Number is required field and may be in any valid phone number format.")
    private String phone;

    @NotBlank(message = "Website is a required field")
    @Pattern(regexp = "^(http:\\/\\/|https:\\/\\/)?(www\\.)?[a-zA-Z0-9-_\\.]+" +
            "\\.[a-zA-Z]+(:\\d+)?(\\/[a-zA-Z\\d\\.\\-_]*)*[a-zA-Z.!@#$%&=-_'\":,.?\\d*)(]*$", message = "Website should have a valid format")
    private String website;

    @Valid
    private AddressDto address;
    private CompanyStatus companyStatus;
}
