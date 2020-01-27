// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.HierarchicalMessageSource;

public class DelegatingMessageSource extends MessageSourceSupport implements HierarchicalMessageSource
{
    private MessageSource parentMessageSource;
    
    @Override
    public void setParentMessageSource(final MessageSource parent) {
        this.parentMessageSource = parent;
    }
    
    @Override
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }
    
    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, defaultMessage, locale);
        }
        return this.renderDefaultMessage(defaultMessage, args, locale);
    }
    
    @Override
    public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, locale);
        }
        throw new NoSuchMessageException(code, locale);
    }
    
    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(resolvable, locale);
        }
        if (resolvable.getDefaultMessage() != null) {
            return this.renderDefaultMessage(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
        }
        final String[] codes = resolvable.getCodes();
        final String code = (codes != null && codes.length > 0) ? codes[0] : null;
        throw new NoSuchMessageException(code, locale);
    }
}
