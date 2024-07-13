package com.mohamed.halim.cacheaop.aop;

import com.mohamed.halim.cacheaop.service.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
        var key = constructKey(cacheAnnotation.prefix(), cacheAnnotation.key(), method, joinPoint.getArgs());
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

    @Before("@annotation(CachedEvict)")
    public void cacheEvict(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var cacheAnnotation = method.getAnnotation(CachedEvict.class);
        var key = constructKey(cacheAnnotation.prefix(), cacheAnnotation.key(), method, joinPoint.getArgs());
        log.info("evict cache: {}", key);
        cacheManager.evict(key);
    }

    @Around("@annotation(CachedRefresh)")
    public Object CachedRefresh(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var cacheAnnotation = method.getAnnotation(CachedRefresh.class);
        var key = constructKey(cacheAnnotation.prefix(), cacheAnnotation.key(), method, joinPoint.getArgs());

        Object res = joinPoint.proceed();
        cacheManager.put(key, res);
        log.info("refresh cache: {}", key);
        return res;

    }

    private String constructKey(String prefix, String[] keys, Method method, Object[] args) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(prefix)) {
            builder.append(method.getName());
        } else {
            builder.append(prefix);
        }

        if (keys == null || keys.length == 0) {
            var key = Arrays.stream(args).map(Object::toString).reduce("", (s1, s2) -> s1 + "::" + s2);
            builder.append(key);
        } else {
            var paramMap = new HashMap<String, Object>();
            for (int i = 0; i < args.length; i++) {
                paramMap.put(method.getParameters()[i].getName(), args[i]);
            }
            var key = Arrays.stream(keys).map(k -> getValue(k, paramMap)).reduce("", (s1, s2) -> s1 + "::" + s2);
            builder.append(key);
        }
        return builder.toString();
    }

    @SneakyThrows
    private Object getValue(String key, HashMap<String, Object> paramMap) {
        String[] fields = key.split("\\.");
        if (fields.length == 1) {
            return paramMap.get(key);
        }
        Object obj = paramMap.get(fields[0]);
        fields[1] = StringUtils.capitalize(fields[1]);
        return obj.getClass().getMethod("get" + fields[1]).invoke(obj);
    }
}
