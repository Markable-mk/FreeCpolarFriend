package com.itmark.service.dataCache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/11/21 20:28
 */

@Service
public class DataCacheServiceImpl implements DataCacheService{

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(255)
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();


    @Override
    public void saveValue(String key, String val) {
        cache.put(key, val);
    }

    @Override
    public void deleteValue(String key) {
        cache.invalidate(key);
    }

    @Override
    public Object selectValueObject(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public String selectValueString(String key) {
        return cache.getIfPresent(key);
    }

}
