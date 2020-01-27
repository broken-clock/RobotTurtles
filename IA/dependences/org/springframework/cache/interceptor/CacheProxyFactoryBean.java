// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;

public class CacheProxyFactoryBean extends AbstractSingletonProxyFactoryBean
{
    private final CacheInterceptor cachingInterceptor;
    private Pointcut pointcut;
    
    public CacheProxyFactoryBean() {
        this.cachingInterceptor = new CacheInterceptor();
    }
    
    public void setPointcut(final Pointcut pointcut) {
        this.pointcut = pointcut;
    }
    
    @Override
    protected Object createMainInterceptor() {
        this.cachingInterceptor.afterPropertiesSet();
        if (this.pointcut == null) {
            throw new UnsupportedOperationException();
        }
        return new DefaultPointcutAdvisor(this.pointcut, this.cachingInterceptor);
    }
    
    public void setCacheOperationSources(final CacheOperationSource... cacheOperationSources) {
        this.cachingInterceptor.setCacheOperationSources(cacheOperationSources);
    }
}
