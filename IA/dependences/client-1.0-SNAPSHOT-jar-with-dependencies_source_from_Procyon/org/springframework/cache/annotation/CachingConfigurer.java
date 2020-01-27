// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.CacheManager;

public interface CachingConfigurer
{
    CacheManager cacheManager();
    
    KeyGenerator keyGenerator();
}
