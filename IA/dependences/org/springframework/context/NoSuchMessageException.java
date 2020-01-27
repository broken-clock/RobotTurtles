// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import java.util.Locale;

public class NoSuchMessageException extends RuntimeException
{
    public NoSuchMessageException(final String code, final Locale locale) {
        super("No message found under code '" + code + "' for locale '" + locale + "'.");
    }
    
    public NoSuchMessageException(final String code) {
        super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
    }
}
