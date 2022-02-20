package com.tweetapp.backend.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.tweetapp.backend.controller.*.*(..))")
    private void allControllerLevelMethods() {
    }

    @Pointcut("execution(* com.tweetapp.backend.service.tweet.*.*(..))")
    private void tweetServiceLevelMethods() {
    }

    @Pointcut("execution(* com.tweetapp.backend.service.user.*.*(..))")
    private void userServiceLevelMethods() {
    }

    @Around("allControllerLevelMethods()")
    public Object controllerLogging(ProceedingJoinPoint joinPoint) throws Throwable {
	final String methodName = joinPoint.getSignature().getName();
	final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getCanonicalName());
	final long startTime = System.currentTimeMillis();
	logger.info("ENTRY at methodName : {}, startTime : {}", methodName, startTime);
	Object returnedObject = null;
	returnedObject = joinPoint.proceed();
	final long endTime = System.currentTimeMillis();
	logger.info("EXIT from methodName : {}, endTime : {}, timeElapsed : {}", methodName, endTime,
		(endTime - startTime));
	return returnedObject;
    }

    @Around("tweetServiceLevelMethods() || userServiceLevelMethods()")
    public Object serviceLogging(ProceedingJoinPoint joinPoint) throws Throwable {
	final String methodName = joinPoint.getSignature().getName();
	final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getCanonicalName());
	logger.info("Service start methodName : {}", methodName);
	Object returnedObject = null;
	returnedObject = joinPoint.proceed();
	logger.info("Service end methodName : {}", methodName);
	return returnedObject;
    }
}
