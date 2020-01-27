// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import org.springframework.beans.BeansException;
import java.util.Map;

public interface ListableBeanFactory extends BeanFactory
{
    boolean containsBeanDefinition(final String p0);
    
    int getBeanDefinitionCount();
    
    String[] getBeanDefinitionNames();
    
    String[] getBeanNamesForType(final Class<?> p0);
    
    String[] getBeanNamesForType(final Class<?> p0, final boolean p1, final boolean p2);
    
     <T> Map<String, T> getBeansOfType(final Class<T> p0) throws BeansException;
    
     <T> Map<String, T> getBeansOfType(final Class<T> p0, final boolean p1, final boolean p2) throws BeansException;
    
    String[] getBeanNamesForAnnotation(final Class<? extends Annotation> p0);
    
    Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> p0) throws BeansException;
    
     <A extends Annotation> A findAnnotationOnBean(final String p0, final Class<A> p1) throws NoSuchBeanDefinitionException;
}
