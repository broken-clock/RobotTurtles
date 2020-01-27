// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.support;

import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Iterator;
import org.springframework.cache.Cache;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;

public class CompositeCacheManager implements CacheManager, InitializingBean
{
    private final List<CacheManager> cacheManagers;
    private boolean fallbackToNoOpCache;
    
    public CompositeCacheManager() {
        this.cacheManagers = new ArrayList<CacheManager>();
        this.fallbackToNoOpCache = false;
    }
    
    public CompositeCacheManager(final CacheManager... cacheManagers) {
        this.cacheManagers = new ArrayList<CacheManager>();
        this.fallbackToNoOpCache = false;
        this.setCacheManagers(Arrays.asList(cacheManagers));
    }
    
    public void setCacheManagers(final Collection<CacheManager> cacheManagers) {
        this.cacheManagers.addAll(cacheManagers);
    }
    
    public void setFallbackToNoOpCache(final boolean fallbackToNoOpCache) {
        this.fallbackToNoOpCache = fallbackToNoOpCache;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.fallbackToNoOpCache) {
            this.cacheManagers.add(new NoOpCacheManager());
        }
    }
    
    @Override
    public Cache getCache(final String name) {
        for (final CacheManager cacheManager : this.cacheManagers) {
            final Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                return cache;
            }
        }
        return null;
    }
    
    @Override
    public Collection<String> getCacheNames() {
        final Set<String> names = new LinkedHashSet<String>();
        for (final CacheManager manager : this.cacheManagers) {
            names.addAll(manager.getCacheNames());
        }
        return (Collection<String>)Collections.unmodifiableSet((Set<?>)names);
    }
}
