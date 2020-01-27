// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.TypeConverter;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory
{
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    
     <T> T createBean(final Class<T> p0) throws BeansException;
    
    void autowireBean(final Object p0) throws BeansException;
    
    Object configureBean(final Object p0, final String p1) throws BeansException;
    
    Object resolveDependency(final DependencyDescriptor p0, final String p1) throws BeansException;
    
    Object createBean(final Class<?> p0, final int p1, final boolean p2) throws BeansException;
    
    Object autowire(final Class<?> p0, final int p1, final boolean p2) throws BeansException;
    
    void autowireBeanProperties(final Object p0, final int p1, final boolean p2) throws BeansException;
    
    void applyBeanPropertyValues(final Object p0, final String p1) throws BeansException;
    
    Object initializeBean(final Object p0, final String p1) throws BeansException;
    
    Object applyBeanPostProcessorsBeforeInitialization(final Object p0, final String p1) throws BeansException;
    
    Object applyBeanPostProcessorsAfterInitialization(final Object p0, final String p1) throws BeansException;
    
    void destroyBean(final Object p0);
    
    Object resolveDependency(final DependencyDescriptor p0, final String p1, final Set<String> p2, final TypeConverter p3) throws BeansException;
}
