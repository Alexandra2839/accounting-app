package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.cydeo.*.*.*(..))")
    public void anyMethodWithinProject() {}

    @AfterThrowing(pointcut = "anyMethodWithinProject()", throwing = "exception")
    public void afterReturningAnyProjectAndTaskControllerAdvice(JoinPoint joinPoint, Exception exception) {
        log.info("Method: {}, Exception: {}, Message: {}"
                , joinPoint.getSignature().toShortString()
                , exception.getClass().getName()
                , exception.getMessage());
    }

}
