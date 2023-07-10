package com.cydeo.dto;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceProductDto {

    private Long id;
    @NotNull(message = "Quantity is required a field.")
    @Range(max = 100, min = 1, message = "Quantity cannot be greater than 100 or less than 1")
    private Integer quantity;

    @NotNull(message = "Price is required a field.")
    @Range(min = 1, message = "Price should be at least 1$")
    private BigDecimal price;

    @NotNull(message = "Tax is required a field.")
    @Range(max = 20, min = 0, message = "Tax should be between 0% and 20%")
    private BigDecimal tax;
    private BigDecimal total;
    private BigDecimal profitLoss;
    private Integer remainingQuantity;
    private InvoiceDto invoice;
    @NotNull(message = "Product Unit is a required field.")
    private ProductDto product;
}
