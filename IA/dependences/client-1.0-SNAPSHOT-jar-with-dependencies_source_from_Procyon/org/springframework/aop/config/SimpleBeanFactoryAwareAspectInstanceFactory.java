// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.aspectj.AspectInstanceFactory;

public class SimpleBeanFactoryAwareAspectInstanceFactory implements AspectInstanceFactory, BeanFactoryAware
{
    private String aspectBeanName;
    private BeanFactory beanFactory;
    
    public void setAspectBeanName(final String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (!StringUtils.hasText(this.aspectBeanName)) {
            throw new IllegalArgumentException("'aspectBeanName' is required");
        }
    }
    
    @Override
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.aspectBeanName);
    }
    
    @Override
    public ClassLoader getAspectClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }
    
    @Override
    public int getOrder() {
        if (this.beanFactory.isSingleton(this.aspectBeanName) && this.beanFactory.isTypeMatch(this.aspectBeanName, Ordered.class)) {
            return ((Ordered)this.beanFactory.getBean(this.aspectBeanName)).getOrder();
        }
        return Integer.MAX_VALUE;
    }
}
