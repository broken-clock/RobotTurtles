// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Collection;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.Assert;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.Map;
import org.springframework.core.SimpleAliasRegistry;

public class SimpleBeanDefinitionRegistry extends SimpleAliasRegistry implements BeanDefinitionRegistry
{
    private final Map<String, BeanDefinition> beanDefinitionMap;
    
    public SimpleBeanDefinitionRegistry() {
        this.beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
    }
    
    @Override
    public void registerBeanDefinition(final String beanName, final BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }
    
    @Override
    public void removeBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        if (this.beanDefinitionMap.remove(beanName) == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
    }
    
    @Override
    public BeanDefinition getBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        final BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }
    
    @Override
    public boolean containsBeanDefinition(final String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beanDefinitionMap.keySet());
    }
    
    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
    
    @Override
    public boolean isBeanNameInUse(final String beanName) {
        return this.isAlias(beanName) || this.containsBeanDefinition(beanName);
    }
}
