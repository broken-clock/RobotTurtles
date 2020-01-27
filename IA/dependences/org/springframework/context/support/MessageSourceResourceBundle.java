// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.Enumeration;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;
import java.util.Locale;
import org.springframework.context.MessageSource;
import java.util.ResourceBundle;

public class MessageSourceResourceBundle extends ResourceBundle
{
    private final MessageSource messageSource;
    private final Locale locale;
    
    public MessageSourceResourceBundle(final MessageSource source, final Locale locale) {
        Assert.notNull(source, "MessageSource must not be null");
        this.messageSource = source;
        this.locale = locale;
    }
    
    public MessageSourceResourceBundle(final MessageSource source, final Locale locale, final ResourceBundle parent) {
        this(source, locale);
        this.setParent(parent);
    }
    
    @Override
    protected Object handleGetObject(final String key) {
        try {
            return this.messageSource.getMessage(key, null, this.locale);
        }
        catch (NoSuchMessageException ex) {
            return null;
        }
    }
    
    @Override
    public boolean containsKey(final String key) {
        try {
            this.messageSource.getMessage(key, null, this.locale);
            return true;
        }
        catch (NoSuchMessageException ex) {
            return false;
        }
    }
    
    @Override
    public Enumeration<String> getKeys() {
        throw new UnsupportedOperationException("MessageSourceResourceBundle does not support enumerating its keys");
    }
    
    @Override
    public Locale getLocale() {
        return this.locale;
    }
}
