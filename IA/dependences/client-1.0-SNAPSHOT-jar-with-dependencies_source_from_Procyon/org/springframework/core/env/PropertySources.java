// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

public interface PropertySources extends Iterable<PropertySource<?>>
{
    boolean contains(final String p0);
    
    PropertySource<?> get(final String p0);
}
