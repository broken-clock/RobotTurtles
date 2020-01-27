// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.support;

import org.springframework.cache.Cache;
import java.util.Collection;

public class SimpleCacheManager extends AbstractCacheManager
{
    private Collection<? extends Cache> caches;
    
    public void setCaches(final Collection<? extends Cache> caches) {
        this.caches = caches;
    }
    
    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }
}
