package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Table(name = "clients_vendors")
@Entity
public class ClientVendor extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ClientVendorType clientVendorType;

    private String clientVendorName;

    private String phone;

    private String website;

    @ManyToOne
    private Address address;

    @ManyToOne
    private Company company;

}
