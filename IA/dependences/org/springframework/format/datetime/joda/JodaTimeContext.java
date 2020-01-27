// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

public class JodaTimeContext
{
    private Chronology chronology;
    private DateTimeZone timeZone;
    
    public void setChronology(final Chronology chronology) {
        this.chronology = chronology;
    }
    
    public Chronology getChronology() {
        return this.chronology;
    }
    
    public void setTimeZone(final DateTimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public DateTimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public DateTimeFormatter getFormatter(DateTimeFormatter formatter) {
        if (this.chronology != null) {
            formatter = formatter.withChronology(this.chronology);
        }
        if (this.timeZone != null) {
            formatter = formatter.withZone(this.timeZone);
        }
        else {
            final LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                final TimeZone timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone();
                if (timeZone != null) {
                    formatter = formatter.withZone(DateTimeZone.forTimeZone(timeZone));
                }
            }
        }
        return formatter;
    }
}
