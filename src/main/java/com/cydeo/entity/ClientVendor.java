package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Table(name = "clients_vendors")
@Entity
@Where(clause = "is_deleted=false")
public class ClientVendor extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ClientVendorType clientVendorType;

    private String clientVendorName;

    private String phone;

    private String website;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Address address;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "clientVendor")
    private List<Invoice> invoices;

}
