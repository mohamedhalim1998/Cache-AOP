package com.mohamed.halim.cacheaop.aop;

import com.mohamed.halim.cacheaop.service.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

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
        var key = constructKey(cacheAnnotation, method, joinPoint.getArgs());
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

    private String constructKey(Cached cacheAnnotation, Method method, Object[] args) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(cacheAnnotation.prefix())) {
            builder.append(method.getName());
        } else {
            builder.append(cacheAnnotation.prefix());
        }

        if (cacheAnnotation.key() == null || cacheAnnotation.key().length == 0) {
            var key = Arrays.stream(args).map(Object::toString).reduce("", (s1, s2) -> s1 + "::" + s2);
            builder.append(key);
        } else {
            var paramMap = new HashMap<String, String>();
            for (int i = 0; i < args.length; i++) {
                paramMap.put(method.getParameters()[i].getName(), args[i].toString());
            }
            var key = Arrays.stream(cacheAnnotation.key()).map(paramMap::get).reduce("", (s1, s2) -> s1 + "::" + s2);
            builder.append(key);
        }
        return builder.toString();
    }
}
