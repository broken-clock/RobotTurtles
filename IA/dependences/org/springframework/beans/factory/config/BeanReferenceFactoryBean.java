// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartFactoryBean;

@Deprecated
public class BeanReferenceFactoryBean implements SmartFactoryBean<Object>, BeanFactoryAware
{
    private String targetBeanName;
    private BeanFactory beanFactory;
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (this.targetBeanName == null) {
            throw new IllegalArgumentException("'targetBeanName' is required");
        }
        if (!this.beanFactory.containsBean(this.targetBeanName)) {
            throw new NoSuchBeanDefinitionException(this.targetBeanName, this.beanFactory.toString());
        }
    }
    
    @Override
    public Object getObject() throws BeansException {
        if (this.beanFactory == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.beanFactory.getBean(this.targetBeanName);
    }
    
    @Override
    public Class<?> getObjectType() {
        if (this.beanFactory == null) {
            return null;
        }
        return this.beanFactory.getType(this.targetBeanName);
    }
    
    @Override
    public boolean isSingleton() {
        if (this.beanFactory == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.beanFactory.isSingleton(this.targetBeanName);
    }
    
    @Override
    public boolean isPrototype() {
        if (this.beanFactory == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.beanFactory.isPrototype(this.targetBeanName);
    }
    
    @Override
    public boolean isEagerInit() {
        return false;
    }
}
