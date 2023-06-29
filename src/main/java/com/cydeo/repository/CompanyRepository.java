package com.cydeo.repository;

import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    @Query(value = "SELECT DISTINCT FROM companies c JOIN users u ON c.id=u.company_id" +
            " WHERE u.username=?1", nativeQuery = true)
    Company findCompanyByUser(String username);


}
