// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import java.util.Collections;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.List;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.CollectionUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import org.springframework.cache.Cache;
import java.util.Collection;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.CacheManager;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;

public abstract class CacheAspectSupport implements InitializingBean
{
    protected final Log logger;
    private final ExpressionEvaluator evaluator;
    private CacheManager cacheManager;
    private CacheOperationSource cacheOperationSource;
    private KeyGenerator keyGenerator;
    private boolean initialized;
    
    public CacheAspectSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.evaluator = new ExpressionEvaluator();
        this.keyGenerator = new SimpleKeyGenerator();
        this.initialized = false;
    }
    
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }
    
    public void setCacheOperationSources(final CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources);
        this.cacheOperationSource = ((cacheOperationSources.length > 1) ? new CompositeCacheOperationSource(cacheOperationSources) : cacheOperationSources[0]);
    }
    
    public CacheOperationSource getCacheOperationSource() {
        return this.cacheOperationSource;
    }
    
    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
    
    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }
    
    @Override
    public void afterPropertiesSet() {
        Assert.state(this.cacheManager != null, "'cacheManager' is required");
        Assert.state(this.cacheOperationSource != null, "The 'cacheOperationSources' property is required: If there are no cacheable methods, then don't use a cache aspect.");
        this.initialized = true;
    }
    
    protected String methodIdentification(final Method method, final Class<?> targetClass) {
        final Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return ClassUtils.getQualifiedMethodName(specificMethod);
    }
    
    protected Collection<? extends Cache> getCaches(final CacheOperation operation) {
        final Set<String> cacheNames = operation.getCacheNames();
        final Collection<Cache> caches = new ArrayList<Cache>(cacheNames.size());
        for (final String cacheName : cacheNames) {
            final Cache cache = this.cacheManager.getCache(cacheName);
            Assert.notNull(cache, "Cannot find cache named '" + cacheName + "' for " + operation);
            caches.add(cache);
        }
        return caches;
    }
    
    protected CacheOperationContext getOperationContext(final CacheOperation operation, final Method method, final Object[] args, final Object target, final Class<?> targetClass) {
        return new CacheOperationContext(operation, method, args, target, targetClass);
    }
    
    protected Object execute(final Invoker invoker, final Object target, final Method method, final Object[] args) {
        if (this.initialized) {
            final Class<?> targetClass = this.getTargetClass(target);
            final Collection<CacheOperation> operations = this.getCacheOperationSource().getCacheOperations(method, targetClass);
            if (!CollectionUtils.isEmpty(operations)) {
                return this.execute(invoker, new CacheOperationContexts(operations, method, args, target, targetClass));
            }
        }
        return invoker.invoke();
    }
    
    private Class<?> getTargetClass(final Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }
    
    private Object execute(final Invoker invoker, final CacheOperationContexts contexts) {
        this.processCacheEvicts(contexts.get(CacheEvictOperation.class), true, ExpressionEvaluator.NO_RESULT);
        final List<CachePutRequest> cachePutRequests = new ArrayList<CachePutRequest>();
        this.collectPutRequests(contexts.get(CacheableOperation.class), ExpressionEvaluator.NO_RESULT, cachePutRequests, true);
        Cache.ValueWrapper result = null;
        if (cachePutRequests.isEmpty() && contexts.get(CachePutOperation.class).isEmpty()) {
            result = this.findCachedResult(contexts.get(CacheableOperation.class));
        }
        if (result == null) {
            result = new SimpleValueWrapper(invoker.invoke());
        }
        this.collectPutRequests(contexts.get(CachePutOperation.class), result.get(), cachePutRequests, false);
        for (final CachePutRequest cachePutRequest : cachePutRequests) {
            cachePutRequest.apply(result.get());
        }
        this.processCacheEvicts(contexts.get(CacheEvictOperation.class), false, result.get());
        return result.get();
    }
    
    private void processCacheEvicts(final Collection<CacheOperationContext> contexts, final boolean beforeInvocation, final Object result) {
        for (final CacheOperationContext context : contexts) {
            final CacheEvictOperation operation = (CacheEvictOperation)context.operation;
            if (beforeInvocation == operation.isBeforeInvocation() && this.isConditionPassing(context, result)) {
                this.performCacheEvict(context, operation, result);
            }
        }
    }
    
    private void performCacheEvict(final CacheOperationContext context, final CacheEvictOperation operation, final Object result) {
        Object key = null;
        for (final Cache cache : context.getCaches()) {
            if (operation.isCacheWide()) {
                this.logInvalidating(context, operation, null);
                cache.clear();
            }
            else {
                if (key == null) {
                    key = context.generateKey(result);
                }
                this.logInvalidating(context, operation, key);
                cache.evict(key);
            }
        }
    }
    
    private void logInvalidating(final CacheOperationContext context, final CacheEvictOperation operation, final Object key) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invalidating " + ((key != null) ? ("cache key [" + key + "]") : "entire cache") + " for operation " + operation + " on method " + context.method);
        }
    }
    
    private void collectPutRequests(final Collection<CacheOperationContext> contexts, final Object result, final Collection<CachePutRequest> putRequests, final boolean whenNotInCache) {
        for (final CacheOperationContext context : contexts) {
            if (this.isConditionPassing(context, result)) {
                final Object key = this.generateKey(context, result);
                if (whenNotInCache && this.findInAnyCaches(contexts, key) != null) {
                    continue;
                }
                putRequests.add(new CachePutRequest(context, key));
            }
        }
    }
    
    private Cache.ValueWrapper findCachedResult(final Collection<CacheOperationContext> contexts) {
        Cache.ValueWrapper result = null;
        for (final CacheOperationContext context : contexts) {
            if (this.isConditionPassing(context, ExpressionEvaluator.NO_RESULT) && result == null) {
                result = this.findInCaches(context, this.generateKey(context, ExpressionEvaluator.NO_RESULT));
            }
        }
        return result;
    }
    
    private Cache.ValueWrapper findInAnyCaches(final Collection<CacheOperationContext> contexts, final Object key) {
        for (final CacheOperationContext context : contexts) {
            final Cache.ValueWrapper wrapper = this.findInCaches(context, key);
            if (wrapper != null) {
                return wrapper;
            }
        }
        return null;
    }
    
    private Cache.ValueWrapper findInCaches(final CacheOperationContext context, final Object key) {
        for (final Cache cache : context.getCaches()) {
            final Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                return wrapper;
            }
        }
        return null;
    }
    
    private boolean isConditionPassing(final CacheOperationContext context, final Object result) {
        final boolean passing = context.isConditionPassing(result);
        if (!passing && this.logger.isTraceEnabled()) {
            this.logger.trace("Cache condition failed on method " + context.method + " for operation " + context.operation);
        }
        return passing;
    }
    
    private Object generateKey(final CacheOperationContext context, final Object result) {
        final Object key = context.generateKey(result);
        Assert.notNull(key, "Null key returned for cache operation (maybe you are using named params on classes without debug info?) " + context.operation);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Computed cache key " + key + " for operation " + context.operation);
        }
        return key;
    }
    
    private class CacheOperationContexts
    {
        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts;
        
        public CacheOperationContexts(final Collection<? extends CacheOperation> operations, final Method method, final Object[] args, final Object target, final Class<?> targetClass) {
            this.contexts = new LinkedMultiValueMap<Class<? extends CacheOperation>, CacheOperationContext>();
            for (final CacheOperation operation : operations) {
                this.contexts.add(operation.getClass(), CacheAspectSupport.this.getOperationContext(operation, method, args, target, targetClass));
            }
        }
        
        public Collection<CacheOperationContext> get(final Class<? extends CacheOperation> operationClass) {
            final Collection<CacheOperationContext> result = this.contexts.get(operationClass);
            return (Collection<CacheOperationContext>)((result != null) ? result : Collections.emptyList());
        }
    }
    
    protected class CacheOperationContext
    {
        private final CacheOperation operation;
        private final Method method;
        private final Object[] args;
        private final Object target;
        private final Class<?> targetClass;
        private final Collection<? extends Cache> caches;
        
        public CacheOperationContext(final CacheOperation operation, final Method method, final Object[] args, final Object target, final Class<?> targetClass) {
            this.operation = operation;
            this.method = method;
            this.args = this.extractArgs(method, args);
            this.target = target;
            this.targetClass = targetClass;
            this.caches = CacheAspectSupport.this.getCaches(operation);
        }
        
        private Object[] extractArgs(final Method method, final Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            final Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            final Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }
        
        protected boolean isConditionPassing(final Object result) {
            if (StringUtils.hasText(this.operation.getCondition())) {
                final EvaluationContext evaluationContext = this.createEvaluationContext(result);
                return CacheAspectSupport.this.evaluator.condition(this.operation.getCondition(), this.method, evaluationContext);
            }
            return true;
        }
        
        protected boolean canPutToCache(final Object value) {
            String unless = "";
            if (this.operation instanceof CacheableOperation) {
                unless = ((CacheableOperation)this.operation).getUnless();
            }
            else if (this.operation instanceof CachePutOperation) {
                unless = ((CachePutOperation)this.operation).getUnless();
            }
            if (StringUtils.hasText(unless)) {
                final EvaluationContext evaluationContext = this.createEvaluationContext(value);
                return !CacheAspectSupport.this.evaluator.unless(unless, this.method, evaluationContext);
            }
            return true;
        }
        
        protected Object generateKey(final Object result) {
            if (StringUtils.hasText(this.operation.getKey())) {
                final EvaluationContext evaluationContext = this.createEvaluationContext(result);
                return CacheAspectSupport.this.evaluator.key(this.operation.getKey(), this.method, evaluationContext);
            }
            return CacheAspectSupport.this.keyGenerator.generate(this.target, this.method, this.args);
        }
        
        private EvaluationContext createEvaluationContext(final Object result) {
            return CacheAspectSupport.this.evaluator.createEvaluationContext(this.caches, this.method, this.args, this.target, this.targetClass, result);
        }
        
        protected Collection<? extends Cache> getCaches() {
            return this.caches;
        }
    }
    
    private static class CachePutRequest
    {
        private final CacheOperationContext context;
        private final Object key;
        
        public CachePutRequest(final CacheOperationContext context, final Object key) {
            this.context = context;
            this.key = key;
        }
        
        public void apply(final Object result) {
            if (this.context.canPutToCache(result)) {
                for (final Cache cache : this.context.getCaches()) {
                    cache.put(this.key, result);
                }
            }
        }
    }
    
    public interface Invoker
    {
        Object invoke();
    }
}
