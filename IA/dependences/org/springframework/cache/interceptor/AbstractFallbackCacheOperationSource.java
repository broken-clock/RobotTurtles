// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.util.ObjectUtils;
import java.util.Collections;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.util.Collection;

public abstract class AbstractFallbackCacheOperationSource implements CacheOperationSource
{
    private static final Collection<CacheOperation> NULL_CACHING_ATTRIBUTE;
    protected final Log logger;
    final Map<Object, Collection<CacheOperation>> attributeCache;
    
    public AbstractFallbackCacheOperationSource() {
        this.logger = LogFactory.getLog(this.getClass());
        this.attributeCache = new ConcurrentHashMap<Object, Collection<CacheOperation>>(1024);
    }
    
    @Override
    public Collection<CacheOperation> getCacheOperations(final Method method, final Class<?> targetClass) {
        final Object cacheKey = this.getCacheKey(method, targetClass);
        final Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);
        if (cached == null) {
            final Collection<CacheOperation> cacheOps = this.computeCacheOperations(method, targetClass);
            if (cacheOps == null) {
                this.attributeCache.put(cacheKey, AbstractFallbackCacheOperationSource.NULL_CACHING_ATTRIBUTE);
            }
            else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
                }
                this.attributeCache.put(cacheKey, cacheOps);
            }
            return cacheOps;
        }
        if (cached == AbstractFallbackCacheOperationSource.NULL_CACHING_ATTRIBUTE) {
            return null;
        }
        return cached;
    }
    
    protected Object getCacheKey(final Method method, final Class<?> targetClass) {
        return new DefaultCacheKey(method, targetClass);
    }
    
    private Collection<CacheOperation> computeCacheOperations(final Method method, final Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        Collection<CacheOperation> opDef = this.findCacheOperations(specificMethod);
        if (opDef != null) {
            return opDef;
        }
        opDef = this.findCacheOperations(specificMethod.getDeclaringClass());
        if (opDef != null) {
            return opDef;
        }
        if (specificMethod == method) {
            return null;
        }
        opDef = this.findCacheOperations(method);
        if (opDef != null) {
            return opDef;
        }
        return this.findCacheOperations(method.getDeclaringClass());
    }
    
    protected abstract Collection<CacheOperation> findCacheOperations(final Method p0);
    
    protected abstract Collection<CacheOperation> findCacheOperations(final Class<?> p0);
    
    protected boolean allowPublicMethodsOnly() {
        return false;
    }
    
    static {
        NULL_CACHING_ATTRIBUTE = Collections.emptyList();
    }
    
    private static class DefaultCacheKey
    {
        private final Method method;
        private final Class<?> targetClass;
        
        public DefaultCacheKey(final Method method, final Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DefaultCacheKey)) {
                return false;
            }
            final DefaultCacheKey otherKey = (DefaultCacheKey)other;
            return this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass);
        }
        
        @Override
        public int hashCode() {
            return this.method.hashCode() * 29 + ((this.targetClass != null) ? this.targetClass.hashCode() : 0);
        }
    }
}
