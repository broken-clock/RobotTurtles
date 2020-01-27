// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

public interface PropertyResolver
{
    boolean containsProperty(final String p0);
    
    String getProperty(final String p0);
    
    String getProperty(final String p0, final String p1);
    
     <T> T getProperty(final String p0, final Class<T> p1);
    
     <T> T getProperty(final String p0, final Class<T> p1, final T p2);
    
     <T> Class<T> getPropertyAsClass(final String p0, final Class<T> p1);
    
    String getRequiredProperty(final String p0) throws IllegalStateException;
    
     <T> T getRequiredProperty(final String p0, final Class<T> p1) throws IllegalStateException;
    
    String resolvePlaceholders(final String p0);
    
    String resolveRequiredPlaceholders(final String p0) throws IllegalArgumentException;
}
