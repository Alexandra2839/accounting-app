package com.cydeo.repository;

import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "SELECT * FROM companies WHERE id != 1 ORDER BY company_status, title ASC", nativeQuery = true)
    List<Company> findAllBesidesId1OrderedByStatusAndTitle();


    Optional<Company> findByTitle(String title);


}
