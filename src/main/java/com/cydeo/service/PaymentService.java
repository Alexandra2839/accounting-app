package com.cydeo.service;

import com.cydeo.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
   void findPaymentsIfNotExist(int year);
   List<PaymentDto> findAllByYear(int year);
   PaymentDto getPaymentById(Long id);
}
