// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import org.springframework.context.support.MessageSourceResourceBundle;
import java.util.ResourceBundle;
import java.util.Locale;
import org.springframework.util.Assert;
import org.springframework.context.MessageSource;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

public class MessageSourceResourceBundleLocator implements ResourceBundleLocator
{
    private final MessageSource messageSource;
    
    public MessageSourceResourceBundleLocator(final MessageSource messageSource) {
        Assert.notNull(messageSource, "MessageSource must not be null");
        this.messageSource = messageSource;
    }
    
    public ResourceBundle getResourceBundle(final Locale locale) {
        return new MessageSourceResourceBundle(this.messageSource, locale);
    }
}
