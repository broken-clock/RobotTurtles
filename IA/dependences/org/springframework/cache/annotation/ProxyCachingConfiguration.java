// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Bean;
import org.aopalliance.aop.Advice;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyCachingConfiguration extends AbstractCachingConfiguration
{
    @Bean(name = { "org.springframework.cache.config.internalCacheAdvisor" })
    @Role(2)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        final BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(this.cacheOperationSource());
        advisor.setAdvice(this.cacheInterceptor());
        advisor.setOrder(this.enableCaching.getNumber("order"));
        return advisor;
    }
    
    @Bean
    @Role(2)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource();
    }
    
    @Bean
    @Role(2)
    public CacheInterceptor cacheInterceptor() {
        final CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSources(this.cacheOperationSource());
        if (this.cacheManager != null) {
            interceptor.setCacheManager(this.cacheManager);
        }
        if (this.keyGenerator != null) {
            interceptor.setKeyGenerator(this.keyGenerator);
        }
        return interceptor;
    }
}
