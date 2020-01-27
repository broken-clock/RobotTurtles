// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class BeanFactoryCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor
{
    private CacheOperationSource cacheOperationSource;
    private final CacheOperationSourcePointcut pointcut;
    
    public BeanFactoryCacheOperationSourceAdvisor() {
        this.pointcut = new CacheOperationSourcePointcut() {
            @Override
            protected CacheOperationSource getCacheOperationSource() {
                return BeanFactoryCacheOperationSourceAdvisor.this.cacheOperationSource;
            }
        };
    }
    
    public void setCacheOperationSource(final CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }
    
    public void setClassFilter(final ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
