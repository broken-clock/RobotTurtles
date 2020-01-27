// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

public class BeanIsNotAFactoryException extends BeanNotOfRequiredTypeException
{
    public BeanIsNotAFactoryException(final String name, final Class<?> actualType) {
        super(name, FactoryBean.class, actualType);
    }
}
