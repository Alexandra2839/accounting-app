package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Payment;
import com.cydeo.enums.Months;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.PaymentRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MapperUtil mapperUtil, CompanyService companyService) {
        this.paymentRepository = paymentRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public void findPaymentsIfNotExist(int year) {
        LocalDate thisYear = LocalDate.now().withYear(year);
        int year1 = thisYear.getYear();

        CompanyDto companyDtoByLoggedInUser = companyService.getCompanyDtoByLoggedInUser();

        List<Payment> payments = paymentRepository.findAllByYearAndCompanyId(year,
                companyDtoByLoggedInUser.getId());

        Company company = mapperUtil.convert(companyDtoByLoggedInUser, new Company());

        if (payments.size() == 0){
            for (Months month : Months.values()) {
                Payment payment = new Payment();
                payment.setMonth(month);
                payment.setYear(year1);
                payment.setAmount(BigDecimal.valueOf(250));
                payment.setPaid(false);
                payment.setCompany(company);
                paymentRepository.save(payment);
            }
        }
    }

    @Override
    public List<PaymentDto> findAllByYear(int year) {
        CompanyDto companyDtoByLoggedInUser = companyService.getCompanyDtoByLoggedInUser();


        List<Payment> payments = paymentRepository.findAllByYearAndCompanyId(year,
                companyDtoByLoggedInUser.getId());

        List<PaymentDto> collect = payments.stream()
                .map(payment -> mapperUtil.convert(payment, new PaymentDto()))
                .sorted(Comparator.comparing(PaymentDto::getMonth))
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Payment paymentById = paymentRepository.getPaymentById(id);
        return mapperUtil.convert(paymentById, new PaymentDto());
    }
}
