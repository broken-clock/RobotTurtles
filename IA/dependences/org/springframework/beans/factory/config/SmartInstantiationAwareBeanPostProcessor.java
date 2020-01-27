// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;
import org.springframework.beans.BeansException;

public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor
{
    Class<?> predictBeanType(final Class<?> p0, final String p1) throws BeansException;
    
    Constructor<?>[] determineCandidateConstructors(final Class<?> p0, final String p1) throws BeansException;
    
    Object getEarlyBeanReference(final Object p0, final String p1) throws BeansException;
}
