package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.*;


import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product Name is required field.")
    @Size(min = 2, max = 100)
    private String name;

    private Integer quantityInStock;

    @NotNull(message = "Low Limit Alert is a required field."
            + " Low Limit Alert should be at least 1.")
    private Integer lowLimitAlert;

    @NotNull(message = "Please select a Product Unit." +
            "Product Unit is a required field.")
    private ProductUnit productUnit;


    @NotNull(message = "Please select a category." +
            "Category is a required field.")
    private CategoryDto category;

}
