// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.springframework.cache.Cache;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;

public abstract class AbstractCacheManager implements CacheManager, InitializingBean
{
    private final ConcurrentMap<String, Cache> cacheMap;
    private Set<String> cacheNames;
    
    public AbstractCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, Cache>(16);
        this.cacheNames = new LinkedHashSet<String>(16);
    }
    
    @Override
    public void afterPropertiesSet() {
        final Collection<? extends Cache> caches = this.loadCaches();
        this.cacheMap.clear();
        this.cacheNames.clear();
        for (final Cache cache : caches) {
            this.cacheMap.put(cache.getName(), this.decorateCache(cache));
            this.cacheNames.add(cache.getName());
        }
    }
    
    protected final void addCache(final Cache cache) {
        this.cacheMap.put(cache.getName(), this.decorateCache(cache));
        this.cacheNames.add(cache.getName());
    }
    
    protected Cache decorateCache(final Cache cache) {
        return cache;
    }
    
    @Override
    public Cache getCache(final String name) {
        return this.cacheMap.get(name);
    }
    
    @Override
    public Collection<String> getCacheNames() {
        return (Collection<String>)Collections.unmodifiableSet((Set<?>)this.cacheNames);
    }
    
    protected abstract Collection<? extends Cache> loadCaches();
}
