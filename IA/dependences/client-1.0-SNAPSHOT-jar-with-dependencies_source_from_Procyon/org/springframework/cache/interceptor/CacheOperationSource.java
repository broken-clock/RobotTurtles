// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.util.Collection;
import java.lang.reflect.Method;

public interface CacheOperationSource
{
    Collection<CacheOperation> getCacheOperations(final Method p0, final Class<?> p1);
}
