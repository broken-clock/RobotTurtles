// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.style;

public interface ToStringStyler
{
    void styleStart(final StringBuilder p0, final Object p1);
    
    void styleEnd(final StringBuilder p0, final Object p1);
    
    void styleField(final StringBuilder p0, final String p1, final Object p2);
    
    void styleValue(final StringBuilder p0, final Object p1);
    
    void styleFieldSeparator(final StringBuilder p0);
}
