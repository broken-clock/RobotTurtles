// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.BeansException;
import java.lang.reflect.Constructor;

public abstract class InstantiationAwareBeanPostProcessorAdapter implements SmartInstantiationAwareBeanPostProcessor
{
    @Override
    public Class<?> predictBeanType(final Class<?> beanClass, final String beanName) {
        return null;
    }
    
    @Override
    public Constructor<?>[] determineCandidateConstructors(final Class<?> beanClass, final String beanName) throws BeansException {
        return null;
    }
    
    @Override
    public Object getEarlyBeanReference(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
    
    @Override
    public Object postProcessBeforeInstantiation(final Class<?> beanClass, final String beanName) throws BeansException {
        return null;
    }
    
    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        return true;
    }
    
    @Override
    public PropertyValues postProcessPropertyValues(final PropertyValues pvs, final PropertyDescriptor[] pds, final Object bean, final String beanName) throws BeansException {
        return pvs;
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
}
