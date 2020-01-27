// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.util.Assert;
import java.lang.reflect.Method;
import org.springframework.cache.Cache;
import java.util.Collection;

class CacheExpressionRootObject
{
    private final Collection<? extends Cache> caches;
    private final Method method;
    private final Object[] args;
    private final Object target;
    private final Class<?> targetClass;
    
    public CacheExpressionRootObject(final Collection<? extends Cache> caches, final Method method, final Object[] args, final Object target, final Class<?> targetClass) {
        Assert.notNull(method, "Method is required");
        Assert.notNull(targetClass, "targetClass is required");
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.args = args;
        this.caches = caches;
    }
    
    public Collection<? extends Cache> getCaches() {
        return this.caches;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public String getMethodName() {
        return this.method.getName();
    }
    
    public Object[] getArgs() {
        return this.args;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public Class<?> getTargetClass() {
        return this.targetClass;
    }
}
