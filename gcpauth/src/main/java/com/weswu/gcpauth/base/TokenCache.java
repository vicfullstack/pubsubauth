package com.weswu.gcpauth.base;

import com.weswu.gcpauth.config.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weswu
 * @date 2021/5/24
 */
public class TokenCache extends CacheBase{
    private static volatile TokenCache instance;

    private TokenCache(){
        Configuration.ensureLoaded();
        cacheStore = new ConcurrentHashMap<String, CachedObject>(10, 0.9f, 1);
        capacity = 100;
        expireTime = Configuration.CACHED_TOKENS_TTL > 0 ? Configuration.CACHED_TOKENS_TTL : 1800;
    }

    public static TokenCache getInstance(){
        if(instance == null){
            synchronized (TokenCache.class) {
                if(instance == null){
                    instance = new TokenCache();
                }
            }
        }
        return instance;
    }

}
