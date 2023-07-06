package com.cydeo.repository;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct,Long> {

    List<InvoiceProduct> findAllByProductId(Long id);
    List<InvoiceProduct> findByInvoiceId(Long id);
    InvoiceProduct findByInvoiceIdAndId(Long invoiceId, Long productId);
    List<InvoiceProduct> findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyTitle
            (InvoiceStatus status, InvoiceType type, String title);
    List<InvoiceProduct> findAllByInvoice_InvoiceStatusOrderByInvoice_DateDesc(InvoiceStatus status);
}
