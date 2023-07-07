package com.cydeo.aspect;

import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final SecurityService securityService;
    private final CompanyService companyService;

    public LoggingAspect(SecurityService securityService, CompanyService companyService) {
        this.securityService = securityService;
        this.companyService = companyService;
    }

    private UserDto getUser() {
        return securityService.getLoggedInUser();

    }

    @Pointcut("execution(* com.cydeo.controller.CompanyController.activateCompany(..))")
    public void activateCompanyPointcut() {
    }

    @After("activateCompanyPointcut()")
    public void AfterActivateCompanyAdvice(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[0];
        String companyName = companyService.findById(id).getTitle();
        log.info("Method: {}, Company name: {}, User first name: {}, User last name: {}, Username: {}"
                , joinPoint.getSignature().toShortString()
                , companyName
                , getUser().getFirstname()
                , getUser().getLastname()
                , getUser().getUsername());

    }

    @Pointcut("execution(* com.cydeo.controller.CompanyController.deactivateCompany(..))")
    public void deactivateCompanyPointcut() {
    }

    @After("deactivateCompanyPointcut()")
    public void AfterDeactivateCompanyAdvice(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[0];
        String companyName = companyService.findById(id).getTitle();
        log.info("Method: {}, Company name: {}, User first name: {}, User last name: {}, Username: {}"
                , joinPoint.getSignature().toShortString()
                , companyName
                , getUser().getFirstname()
                , getUser().getLastname()
                , getUser().getUsername());

    }


}
