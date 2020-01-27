// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.support;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.springframework.cache.Cache;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.CacheManager;

public class NoOpCacheManager implements CacheManager
{
    private final ConcurrentMap<String, Cache> caches;
    private final Set<String> cacheNames;
    
    public NoOpCacheManager() {
        this.caches = new ConcurrentHashMap<String, Cache>(16);
        this.cacheNames = new LinkedHashSet<String>(16);
    }
    
    @Override
    public Cache getCache(final String name) {
        final Cache cache = this.caches.get(name);
        if (cache == null) {
            this.caches.putIfAbsent(name, new NoOpCache(name));
            synchronized (this.cacheNames) {
                this.cacheNames.add(name);
            }
        }
        return this.caches.get(name);
    }
    
    @Override
    public Collection<String> getCacheNames() {
        synchronized (this.cacheNames) {
            return (Collection<String>)Collections.unmodifiableSet((Set<?>)this.cacheNames);
        }
    }
    
    private static class NoOpCache implements Cache
    {
        private final String name;
        
        public NoOpCache(final String name) {
            this.name = name;
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void evict(final Object key) {
        }
        
        @Override
        public ValueWrapper get(final Object key) {
            return null;
        }
        
        @Override
        public <T> T get(final Object key, final Class<T> type) {
            return null;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public Object getNativeCache() {
            return null;
        }
        
        @Override
        public void put(final Object key, final Object value) {
        }
    }
}
