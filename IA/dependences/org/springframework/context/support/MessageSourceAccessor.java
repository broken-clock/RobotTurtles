// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;
import org.springframework.context.MessageSource;

public class MessageSourceAccessor
{
    private final MessageSource messageSource;
    private final Locale defaultLocale;
    
    public MessageSourceAccessor(final MessageSource messageSource) {
        this.messageSource = messageSource;
        this.defaultLocale = null;
    }
    
    public MessageSourceAccessor(final MessageSource messageSource, final Locale defaultLocale) {
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }
    
    protected Locale getDefaultLocale() {
        return (this.defaultLocale != null) ? this.defaultLocale : LocaleContextHolder.getLocale();
    }
    
    public String getMessage(final String code, final String defaultMessage) {
        return this.messageSource.getMessage(code, null, defaultMessage, this.getDefaultLocale());
    }
    
    public String getMessage(final String code, final String defaultMessage, final Locale locale) {
        return this.messageSource.getMessage(code, null, defaultMessage, locale);
    }
    
    public String getMessage(final String code, final Object[] args, final String defaultMessage) {
        return this.messageSource.getMessage(code, args, defaultMessage, this.getDefaultLocale());
    }
    
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        return this.messageSource.getMessage(code, args, defaultMessage, locale);
    }
    
    public String getMessage(final String code) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, this.getDefaultLocale());
    }
    
    public String getMessage(final String code, final Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, locale);
    }
    
    public String getMessage(final String code, final Object[] args) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, this.getDefaultLocale());
    }
    
    public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, locale);
    }
    
    public String getMessage(final MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, this.getDefaultLocale());
    }
    
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }
}
