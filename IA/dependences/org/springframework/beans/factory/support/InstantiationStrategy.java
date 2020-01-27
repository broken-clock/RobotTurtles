// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public interface InstantiationStrategy
{
    Object instantiate(final RootBeanDefinition p0, final String p1, final BeanFactory p2) throws BeansException;
    
    Object instantiate(final RootBeanDefinition p0, final String p1, final BeanFactory p2, final Constructor<?> p3, final Object[] p4) throws BeansException;
    
    Object instantiate(final RootBeanDefinition p0, final String p1, final BeanFactory p2, final Object p3, final Method p4, final Object[] p5) throws BeansException;
}
