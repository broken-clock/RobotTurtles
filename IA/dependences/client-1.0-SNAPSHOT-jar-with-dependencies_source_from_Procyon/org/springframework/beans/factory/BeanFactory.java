// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface BeanFactory
{
    public static final String FACTORY_BEAN_PREFIX = "&";
    
    Object getBean(final String p0) throws BeansException;
    
     <T> T getBean(final String p0, final Class<T> p1) throws BeansException;
    
     <T> T getBean(final Class<T> p0) throws BeansException;
    
    Object getBean(final String p0, final Object... p1) throws BeansException;
    
    boolean containsBean(final String p0);
    
    boolean isSingleton(final String p0) throws NoSuchBeanDefinitionException;
    
    boolean isPrototype(final String p0) throws NoSuchBeanDefinitionException;
    
    boolean isTypeMatch(final String p0, final Class<?> p1) throws NoSuchBeanDefinitionException;
    
    Class<?> getType(final String p0) throws NoSuchBeanDefinitionException;
    
    String[] getAliases(final String p0);
}
