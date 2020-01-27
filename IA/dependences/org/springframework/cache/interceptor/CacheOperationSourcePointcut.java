// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.util.ObjectUtils;
import java.util.Collection;
import org.springframework.util.CollectionUtils;
import java.lang.reflect.Method;
import java.io.Serializable;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

abstract class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable
{
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        final CacheOperationSource cas = this.getCacheOperationSource();
        return cas != null && !CollectionUtils.isEmpty(cas.getCacheOperations(method, targetClass));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheOperationSourcePointcut)) {
            return false;
        }
        final CacheOperationSourcePointcut otherPc = (CacheOperationSourcePointcut)other;
        return ObjectUtils.nullSafeEquals(this.getCacheOperationSource(), otherPc.getCacheOperationSource());
    }
    
    @Override
    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.getCacheOperationSource();
    }
    
    protected abstract CacheOperationSource getCacheOperationSource();
}
