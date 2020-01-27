// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import javax.validation.MessageInterpolator;

public class LocaleContextMessageInterpolator implements MessageInterpolator
{
    private final MessageInterpolator targetInterpolator;
    
    public LocaleContextMessageInterpolator(final MessageInterpolator targetInterpolator) {
        Assert.notNull(targetInterpolator, "Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }
    
    public String interpolate(final String message, final MessageInterpolator.Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }
    
    public String interpolate(final String message, final MessageInterpolator.Context context, final Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }
}
