package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientVendorRepository extends JpaRepository<ClientVendor, Long> {

    @Query("SELECT cv FROM ClientVendor cv WHERE cv.company.title = ?1 ORDER BY cv.clientVendorType, cv.clientVendorName")
    List<ClientVendor> findAllByCompanyTitleAndSort(String companyTitle);

    @Query("SELECT cv FROM ClientVendor cv WHERE cv.company.title = ?1 and cv.clientVendorType = ?2 ORDER BY cv.clientVendorName")
    List<ClientVendor> findAllByTypeAndSort(String companyTitle, ClientVendorType type);

    Optional<ClientVendor> findByClientVendorName_AndCompany_Title(String clientVendorName, String companyTitle);
}
