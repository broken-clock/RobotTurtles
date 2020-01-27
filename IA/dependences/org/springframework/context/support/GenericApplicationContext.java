// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import java.io.IOException;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry
{
    private final DefaultListableBeanFactory beanFactory;
    private ResourceLoader resourceLoader;
    private boolean refreshed;
    
    public GenericApplicationContext() {
        this.refreshed = false;
        this.beanFactory = new DefaultListableBeanFactory();
    }
    
    public GenericApplicationContext(final DefaultListableBeanFactory beanFactory) {
        this.refreshed = false;
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }
    
    public GenericApplicationContext(final ApplicationContext parent) {
        this();
        this.setParent(parent);
    }
    
    public GenericApplicationContext(final DefaultListableBeanFactory beanFactory, final ApplicationContext parent) {
        this(beanFactory);
        this.setParent(parent);
    }
    
    @Override
    public void setParent(final ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(this.getInternalParentBeanFactory());
    }
    
    @Override
    public void setId(final String id) {
        super.setId(id);
    }
    
    public void setAllowBeanDefinitionOverriding(final boolean allowBeanDefinitionOverriding) {
        this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }
    
    public void setAllowCircularReferences(final boolean allowCircularReferences) {
        this.beanFactory.setAllowCircularReferences(allowCircularReferences);
    }
    
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public Resource getResource(final String location) {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getResource(location);
        }
        return super.getResource(location);
    }
    
    @Override
    public Resource[] getResources(final String locationPattern) throws IOException {
        if (this.resourceLoader instanceof ResourcePatternResolver) {
            return ((ResourcePatternResolver)this.resourceLoader).getResources(locationPattern);
        }
        return super.getResources(locationPattern);
    }
    
    @Override
    protected final void refreshBeanFactory() throws IllegalStateException {
        if (this.refreshed) {
            throw new IllegalStateException("GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        }
        this.beanFactory.setSerializationId(this.getId());
        this.refreshed = true;
    }
    
    @Override
    protected void cancelRefresh(final BeansException ex) {
        this.beanFactory.setSerializationId(null);
        super.cancelRefresh(ex);
    }
    
    @Override
    protected final void closeBeanFactory() {
        this.beanFactory.setSerializationId(null);
    }
    
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }
    
    @Override
    public void registerBeanDefinition(final String beanName, final BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
    
    @Override
    public void removeBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }
    
    @Override
    public boolean isBeanNameInUse(final String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }
    
    @Override
    public void registerAlias(final String beanName, final String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }
    
    @Override
    public void removeAlias(final String alias) {
        this.beanFactory.removeAlias(alias);
    }
    
    @Override
    public boolean isAlias(final String beanName) {
        return this.beanFactory.isAlias(beanName);
    }
}
