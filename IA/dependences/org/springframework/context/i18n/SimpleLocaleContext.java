// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.i18n;

import java.util.Locale;

public class SimpleLocaleContext implements LocaleContext
{
    private final Locale locale;
    
    public SimpleLocaleContext(final Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public String toString() {
        return (this.locale != null) ? this.locale.toString() : "-";
    }
}
