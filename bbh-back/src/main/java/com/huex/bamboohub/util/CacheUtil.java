package com.huex.bamboohub.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheUtil {
    @Autowired CacheManager cacheManager;
    public void clearCache(String value, String key) {
        Cache cache=cacheManager.getCache(value);
        if (cache!=null) {
            cache.evict(key);
        }
    }

    public void clearCache(String value) {
        Cache cache=cacheManager.getCache(value);
        if (cache!=null) {
            cache.clear();
        }
    }

    public void clearCache(List<String> values, String key) {
        for (String value : values) {
            clearCache(value, key);
        }
    }

    public void clearCache(String value, List<String> keys) {
        for (String key : keys) {
            clearCache(value, key);
        }
    }

    public void clearCache(List<String> values) {
        for (String value : values) {
            clearCache(value);
        }
    }

}
