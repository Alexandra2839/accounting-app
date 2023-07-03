package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    List<Invoice> findAllByInvoiceNoStartingWith(String s);
    List<Invoice> findAllByInvoiceType(InvoiceType type);

    @Query("SELECT cv FROM Invoice cv WHERE cv.company.title = ?1 and cv.invoiceType = ?2 ORDER BY cv.clientVendor.clientVendorName")
    List<Invoice> findByCompanyTitleAndInvoiceType(@Param("companyTitle") String companyTitle, @Param("clientVendorType") InvoiceType invoiceType);

    @Query("SELECT cv FROM Invoice cv WHERE cv.company.title = ?1 and cv.invoiceType = ?2 ORDER BY cv.invoiceNo DESC ")
    List<Invoice> findByCompanyTitleAndInvoiceTypeSorted(@Param("companyTitle") String companyTitle, @Param("clientVendorType") InvoiceType invoiceType);

    List<Invoice> findAllByInvoiceTypeAndInvoiceStatusAndCompanyTitle(InvoiceType type, InvoiceStatus status, String title);
}

