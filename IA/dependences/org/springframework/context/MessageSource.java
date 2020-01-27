// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import java.util.Locale;

public interface MessageSource
{
    String getMessage(final String p0, final Object[] p1, final String p2, final Locale p3);
    
    String getMessage(final String p0, final Object[] p1, final Locale p2) throws NoSuchMessageException;
    
    String getMessage(final MessageSourceResolvable p0, final Locale p1) throws NoSuchMessageException;
}
