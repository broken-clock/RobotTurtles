// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.concurrent;

import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.CacheManager;

public class ConcurrentMapCacheManager implements CacheManager
{
    private final ConcurrentMap<String, Cache> cacheMap;
    private boolean dynamic;
    private boolean allowNullValues;
    
    public ConcurrentMapCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, Cache>(16);
        this.dynamic = true;
        this.allowNullValues = true;
    }
    
    public ConcurrentMapCacheManager(final String... cacheNames) {
        this.cacheMap = new ConcurrentHashMap<String, Cache>(16);
        this.dynamic = true;
        this.allowNullValues = true;
        this.setCacheNames(Arrays.asList(cacheNames));
    }
    
    public void setCacheNames(final Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (final String name : cacheNames) {
                this.cacheMap.put(name, this.createConcurrentMapCache(name));
            }
            this.dynamic = false;
        }
    }
    
    public void setAllowNullValues(final boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
    
    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }
    
    @Override
    public Collection<String> getCacheNames() {
        return (Collection<String>)Collections.unmodifiableSet(this.cacheMap.keySet());
    }
    
    @Override
    public Cache getCache(final String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = this.createConcurrentMapCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }
    
    protected Cache createConcurrentMapCache(final String name) {
        return new ConcurrentMapCache(name, this.isAllowNullValues());
    }
}
