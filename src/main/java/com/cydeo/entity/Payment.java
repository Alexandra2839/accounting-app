package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.Months;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {
    private int year;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private boolean isPaid;
    private String companyStripedId;//to store stripe id`s about payment
    private Months month;
    @ManyToOne
    private Company company;
}
