// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.chrono.Chronology;

public class DateTimeContext
{
    private Chronology chronology;
    private ZoneId timeZone;
    
    public void setChronology(final Chronology chronology) {
        this.chronology = chronology;
    }
    
    public Chronology getChronology() {
        return this.chronology;
    }
    
    public void setTimeZone(final ZoneId timeZone) {
        this.timeZone = timeZone;
    }
    
    public ZoneId getTimeZone() {
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
                    formatter = formatter.withZone(timeZone.toZoneId());
                }
            }
        }
        return formatter;
    }
}
