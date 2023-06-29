package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientVendorRepository extends JpaRepository<ClientVendor,Long> {
    Optional<ClientVendor> findById(Long id);

    ClientVendor findByIdAndIsDeleted(Long id, boolean deleted);
}
