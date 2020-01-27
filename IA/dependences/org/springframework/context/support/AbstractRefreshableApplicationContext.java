// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import java.io.IOException;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext
{
    private Boolean allowBeanDefinitionOverriding;
    private Boolean allowCircularReferences;
    private DefaultListableBeanFactory beanFactory;
    private final Object beanFactoryMonitor;
    
    public AbstractRefreshableApplicationContext() {
        this.beanFactoryMonitor = new Object();
    }
    
    public AbstractRefreshableApplicationContext(final ApplicationContext parent) {
        super(parent);
        this.beanFactoryMonitor = new Object();
    }
    
    public void setAllowBeanDefinitionOverriding(final boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }
    
    public void setAllowCircularReferences(final boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }
    
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        if (this.hasBeanFactory()) {
            this.destroyBeans();
            this.closeBeanFactory();
        }
        try {
            final DefaultListableBeanFactory beanFactory = this.createBeanFactory();
            beanFactory.setSerializationId(this.getId());
            this.customizeBeanFactory(beanFactory);
            this.loadBeanDefinitions(beanFactory);
            synchronized (this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        }
        catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + this.getDisplayName(), ex);
        }
    }
    
    @Override
    protected void cancelRefresh(final BeansException ex) {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
            }
        }
        super.cancelRefresh(ex);
    }
    
    @Override
    protected final void closeBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            this.beanFactory.setSerializationId(null);
            this.beanFactory = null;
        }
    }
    
    protected final boolean hasBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            return this.beanFactory != null;
        }
    }
    
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
            }
            return this.beanFactory;
        }
    }
    
    @Override
    protected void assertBeanFactoryActive() {
    }
    
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(this.getInternalParentBeanFactory());
    }
    
    protected void customizeBeanFactory(final DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }
    }
    
    protected abstract void loadBeanDefinitions(final DefaultListableBeanFactory p0) throws BeansException, IOException;
}
