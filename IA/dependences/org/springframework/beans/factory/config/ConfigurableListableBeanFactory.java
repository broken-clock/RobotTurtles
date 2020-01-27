// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ListableBeanFactory;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory
{
    void ignoreDependencyType(final Class<?> p0);
    
    void ignoreDependencyInterface(final Class<?> p0);
    
    void registerResolvableDependency(final Class<?> p0, final Object p1);
    
    boolean isAutowireCandidate(final String p0, final DependencyDescriptor p1) throws NoSuchBeanDefinitionException;
    
    BeanDefinition getBeanDefinition(final String p0) throws NoSuchBeanDefinitionException;
    
    void freezeConfiguration();
    
    boolean isConfigurationFrozen();
    
    void preInstantiateSingletons() throws BeansException;
}
