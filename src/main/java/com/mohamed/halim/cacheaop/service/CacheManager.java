package com.mohamed.halim.cacheaop.service;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class CacheManager {
    private final HashMap<String, Object> cacheMap = new HashMap<>();


    public void put(String key, Object value) {
        cacheMap.put(key, value);
    }

    public Object get(String key) {
        return cacheMap.get(key);
    }

    public boolean exist(String key) {
        return cacheMap.containsKey(key);
    }

}
