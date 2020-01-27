// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

public interface MessageCodesResolver
{
    String[] resolveMessageCodes(final String p0, final String p1);
    
    String[] resolveMessageCodes(final String p0, final String p1, final String p2, final Class<?> p3);
}
