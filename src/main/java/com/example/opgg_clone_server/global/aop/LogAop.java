package com.example.opgg_clone_server.global.aop;

import com.example.opgg_clone_server.global.log.TraceStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.example.opgg_clone_server.global.log.LogTrace;
@Aspect
@Component
@RequiredArgsConstructor
public class LogAop {

    private final LogTrace logTrace;


    @Pointcut("execution(* com.example.opgg_clone_server.domain..*Service*.*(..))")
    public void allService(){};

    @Pointcut("execution(* com.example.opgg_clone_server.domain..*Repository*.*(..))")
    public void allRepository(){};

    @Pointcut("execution(* com.example.opgg_clone_server.domain..*Controller*.*(..))")
    public void allController(){};


    @Around("allService() || allController() || allRepository()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status = null;

        try{
            status = logTrace.begin(joinPoint.getSignature().toShortString());
            Object result = joinPoint.proceed();

            logTrace.end(status);

            return result;
        }catch (Throwable e){
            e.printStackTrace();
            logTrace.exception(status, e);
            throw e;
        }
    }




}