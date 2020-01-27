// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import java.io.Serializable;

public class CompositeCacheOperationSource implements CacheOperationSource, Serializable
{
    private final CacheOperationSource[] cacheOperationSources;
    
    public CompositeCacheOperationSource(final CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources, "cacheOperationSources array must not be empty");
        this.cacheOperationSources = cacheOperationSources;
    }
    
    public final CacheOperationSource[] getCacheOperationSources() {
        return this.cacheOperationSources;
    }
    
    @Override
    public Collection<CacheOperation> getCacheOperations(final Method method, final Class<?> targetClass) {
        Collection<CacheOperation> ops = null;
        for (final CacheOperationSource source : this.cacheOperationSources) {
            final Collection<CacheOperation> cacheOperations = source.getCacheOperations(method, targetClass);
            if (cacheOperations != null) {
                if (ops == null) {
                    ops = new ArrayList<CacheOperation>();
                }
                ops.addAll(cacheOperations);
            }
        }
        return ops;
    }
}
