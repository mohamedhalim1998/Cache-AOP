package com.mohamed.halim.cacheaop.aop;

import com.mohamed.halim.cacheaop.service.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheAop {
    private final CacheManager cacheManager;

    @Around("@annotation(Cached)")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var cacheAnnotation = method.getAnnotation(Cached.class);
        var key = cacheAnnotation.key();
        if (cacheManager.exist(key)) {
            log.info("from cache: {}", key);
            return cacheManager.get(key);
        } else {
            Object res = joinPoint.proceed();
            cacheManager.put(key, res);
            log.info("save to cache: {}", key);
            return res;
        }
    }
}
