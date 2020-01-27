// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public abstract class PropertyAccessorFactory
{
    public static BeanWrapper forBeanPropertyAccess(final Object target) {
        return new BeanWrapperImpl(target);
    }
    
    public static ConfigurablePropertyAccessor forDirectFieldAccess(final Object target) {
        return new DirectFieldAccessor(target);
    }
}
