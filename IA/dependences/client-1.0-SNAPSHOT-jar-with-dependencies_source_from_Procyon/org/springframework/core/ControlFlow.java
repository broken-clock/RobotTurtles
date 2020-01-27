// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public interface ControlFlow
{
    boolean under(final Class<?> p0);
    
    boolean under(final Class<?> p0, final String p1);
    
    boolean underToken(final String p0);
}
