// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ui;

import java.util.Map;
import java.util.Collection;

public interface Model
{
    Model addAttribute(final String p0, final Object p1);
    
    Model addAttribute(final Object p0);
    
    Model addAllAttributes(final Collection<?> p0);
    
    Model addAllAttributes(final Map<String, ?> p0);
    
    Model mergeAttributes(final Map<String, ?> p0);
    
    boolean containsAttribute(final String p0);
    
    Map<String, Object> asMap();
}
