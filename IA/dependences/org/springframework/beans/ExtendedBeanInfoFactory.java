// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.BeanInfo;
import org.springframework.core.Ordered;

public class ExtendedBeanInfoFactory implements BeanInfoFactory, Ordered
{
    @Override
    public BeanInfo getBeanInfo(final Class<?> beanClass) throws IntrospectionException {
        return this.supports(beanClass) ? new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass)) : null;
    }
    
    private boolean supports(final Class<?> beanClass) {
        for (final Method method : beanClass.getMethods()) {
            if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
