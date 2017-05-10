package com.twilio.androidsms.services;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.expiry.Duration;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.expiry.Expirations.timeToLiveExpiration;

@Service
public class OneTimeCodeCache {

    private Cache<String, Integer> cache;

    public OneTimeCodeCache() {
        Duration timeToLive = new Duration(SmsVerificationService.expirationSeconds, TimeUnit.SECONDS);
        CacheManager cacheManager = newCacheManagerBuilder()
                .withCache("otp", newCacheConfigurationBuilder(String.class, Integer.class, heap(100))
                        .withExpiry(timeToLiveExpiration(timeToLive)))
                .build();
        cacheManager.init();

        cache = cacheManager.getCache("otp", String.class, Integer.class);
    }

    public void set(String phone, int code){
        cache.putIfAbsent(phone, code);
    }

    public Optional<Integer> get(String phone){
        Integer value = cache.get(phone);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    public void remove(String phone) {
        cache.remove(phone);
    }
}
