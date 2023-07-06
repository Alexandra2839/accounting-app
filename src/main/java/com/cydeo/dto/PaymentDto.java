package com.cydeo.dto;

import com.cydeo.enums.Months;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Integer year;
    private Months month;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private boolean isPaid;
    private String companyStripedId;
    private String description;
    private CompanyDto company;
}
