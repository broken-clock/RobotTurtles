// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.BeansException;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor
{
    Object postProcessBeforeInstantiation(final Class<?> p0, final String p1) throws BeansException;
    
    boolean postProcessAfterInstantiation(final Object p0, final String p1) throws BeansException;
    
    PropertyValues postProcessPropertyValues(final PropertyValues p0, final PropertyDescriptor[] p1, final Object p2, final String p3) throws BeansException;
}
