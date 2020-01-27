// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;

public interface PropertyAccessor
{
    public static final String NESTED_PROPERTY_SEPARATOR = ".";
    public static final char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
    public static final String PROPERTY_KEY_PREFIX = "[";
    public static final char PROPERTY_KEY_PREFIX_CHAR = '[';
    public static final String PROPERTY_KEY_SUFFIX = "]";
    public static final char PROPERTY_KEY_SUFFIX_CHAR = ']';
    
    boolean isReadableProperty(final String p0);
    
    boolean isWritableProperty(final String p0);
    
    Class<?> getPropertyType(final String p0) throws BeansException;
    
    TypeDescriptor getPropertyTypeDescriptor(final String p0) throws BeansException;
    
    Object getPropertyValue(final String p0) throws BeansException;
    
    void setPropertyValue(final String p0, final Object p1) throws BeansException;
    
    void setPropertyValue(final PropertyValue p0) throws BeansException;
    
    void setPropertyValues(final Map<?, ?> p0) throws BeansException;
    
    void setPropertyValues(final PropertyValues p0) throws BeansException;
    
    void setPropertyValues(final PropertyValues p0, final boolean p1) throws BeansException;
    
    void setPropertyValues(final PropertyValues p0, final boolean p1, final boolean p2) throws BeansException;
}
