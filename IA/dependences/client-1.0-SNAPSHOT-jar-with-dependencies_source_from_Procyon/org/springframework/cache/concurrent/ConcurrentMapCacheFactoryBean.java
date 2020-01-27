// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.concurrent;

import org.springframework.util.StringUtils;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class ConcurrentMapCacheFactoryBean implements FactoryBean<ConcurrentMapCache>, BeanNameAware, InitializingBean
{
    private String name;
    private ConcurrentMap<Object, Object> store;
    private boolean allowNullValues;
    private ConcurrentMapCache cache;
    
    public ConcurrentMapCacheFactoryBean() {
        this.name = "";
        this.allowNullValues = true;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setStore(final ConcurrentMap<Object, Object> store) {
        this.store = store;
    }
    
    public void setAllowNullValues(final boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
    
    @Override
    public void setBeanName(final String beanName) {
        if (!StringUtils.hasLength(this.name)) {
            this.setName(beanName);
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        this.cache = ((this.store != null) ? new ConcurrentMapCache(this.name, this.store, this.allowNullValues) : new ConcurrentMapCache(this.name, this.allowNullValues));
    }
    
    @Override
    public ConcurrentMapCache getObject() {
        return this.cache;
    }
    
    @Override
    public Class<?> getObjectType() {
        return ConcurrentMapCache.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
