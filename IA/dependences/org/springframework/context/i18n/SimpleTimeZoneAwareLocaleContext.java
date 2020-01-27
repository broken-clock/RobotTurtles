// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;

public class SimpleTimeZoneAwareLocaleContext extends SimpleLocaleContext implements TimeZoneAwareLocaleContext
{
    private final TimeZone timeZone;
    
    public SimpleTimeZoneAwareLocaleContext(final Locale locale, final TimeZone timeZone) {
        super(locale);
        this.timeZone = timeZone;
    }
    
    @Override
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + ((this.timeZone != null) ? this.timeZone.toString() : "-");
    }
}
