// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyDescriptor;

public interface BeanWrapper extends ConfigurablePropertyAccessor
{
    Object getWrappedInstance();
    
    Class<?> getWrappedClass();
    
    PropertyDescriptor[] getPropertyDescriptors();
    
    PropertyDescriptor getPropertyDescriptor(final String p0) throws InvalidPropertyException;
    
    void setAutoGrowNestedPaths(final boolean p0);
    
    boolean isAutoGrowNestedPaths();
    
    void setAutoGrowCollectionLimit(final int p0);
    
    int getAutoGrowCollectionLimit();
}
