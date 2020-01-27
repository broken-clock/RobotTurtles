// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public interface AttributeAccessor
{
    void setAttribute(final String p0, final Object p1);
    
    Object getAttribute(final String p0);
    
    Object removeAttribute(final String p0);
    
    boolean hasAttribute(final String p0);
    
    String[] attributeNames();
}
