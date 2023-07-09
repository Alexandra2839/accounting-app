package com.cydeo.repository;


import com.cydeo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByYearAndCompanyId(int year, Long id);
   Payment getPaymentById(Long id);
}
