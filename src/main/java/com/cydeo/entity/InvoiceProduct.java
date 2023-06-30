package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "invoice_products")
public class InvoiceProduct extends BaseEntity {

    private int quantity;
    private BigDecimal price;
    private int tax;
    private BigDecimal total;
    private BigDecimal profitLoss;
    private int remainingQuantity;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST},fetch = FetchType.LAZY)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
