// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import javax.annotation.PostConstruct;
import org.springframework.util.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collection;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;

@Configuration
public abstract class AbstractCachingConfiguration implements ImportAware
{
    protected AnnotationAttributes enableCaching;
    protected CacheManager cacheManager;
    protected KeyGenerator keyGenerator;
    @Autowired(required = false)
    private Collection<CacheManager> cacheManagerBeans;
    @Autowired(required = false)
    private Collection<CachingConfigurer> cachingConfigurers;
    
    @Override
    public void setImportMetadata(final AnnotationMetadata importMetadata) {
        Assert.notNull(this.enableCaching = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCaching.class.getName(), false)), "@EnableCaching is not present on importing class " + importMetadata.getClassName());
    }
    
    @PostConstruct
    protected void reconcileCacheManager() {
        if (!CollectionUtils.isEmpty(this.cachingConfigurers)) {
            final int nConfigurers = this.cachingConfigurers.size();
            if (nConfigurers > 1) {
                throw new IllegalStateException(nConfigurers + " implementations of " + "CachingConfigurer were found when only 1 was expected. " + "Refactor the configuration such that CachingConfigurer is " + "implemented only once or not at all.");
            }
            final CachingConfigurer cachingConfigurer = this.cachingConfigurers.iterator().next();
            this.cacheManager = cachingConfigurer.cacheManager();
            this.keyGenerator = cachingConfigurer.keyGenerator();
        }
        else {
            if (CollectionUtils.isEmpty(this.cacheManagerBeans)) {
                throw new IllegalStateException("No bean of type CacheManager could be found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.");
            }
            final int nManagers = this.cacheManagerBeans.size();
            if (nManagers > 1) {
                throw new IllegalStateException(nManagers + " beans of type CacheManager " + "were found when only 1 was expected. Remove all but one of the " + "CacheManager bean definitions, or implement CachingConfigurer " + "to make explicit which CacheManager should be used for " + "annotation-driven cache management.");
            }
            final CacheManager cacheManager = this.cacheManagerBeans.iterator().next();
            this.cacheManager = cacheManager;
        }
    }
}
