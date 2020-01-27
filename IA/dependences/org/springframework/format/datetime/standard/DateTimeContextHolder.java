// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import org.springframework.core.NamedThreadLocal;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

public final class DateTimeContextHolder
{
    private static final ThreadLocal<DateTimeContext> dateTimeContextHolder;
    
    public static void resetDateTimeContext() {
        DateTimeContextHolder.dateTimeContextHolder.remove();
    }
    
    public static void setDateTimeContext(final DateTimeContext dateTimeContext) {
        if (dateTimeContext == null) {
            resetDateTimeContext();
        }
        else {
            DateTimeContextHolder.dateTimeContextHolder.set(dateTimeContext);
        }
    }
    
    public static DateTimeContext getDateTimeContext() {
        return DateTimeContextHolder.dateTimeContextHolder.get();
    }
    
    public static DateTimeFormatter getFormatter(final DateTimeFormatter formatter, final Locale locale) {
        final DateTimeFormatter formatterToUse = (locale != null) ? formatter.withLocale(locale) : formatter;
        final DateTimeContext context = getDateTimeContext();
        return (context != null) ? context.getFormatter(formatterToUse) : formatterToUse;
    }
    
    static {
        dateTimeContextHolder = new NamedThreadLocal<DateTimeContext>("DateTime Context");
    }
}
