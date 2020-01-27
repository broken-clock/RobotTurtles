// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public interface PropertyValues
{
    PropertyValue[] getPropertyValues();
    
    PropertyValue getPropertyValue(final String p0);
    
    PropertyValues changesSince(final PropertyValues p0);
    
    boolean contains(final String p0);
    
    boolean isEmpty();
}
