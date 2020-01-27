// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import org.springframework.core.NamedThreadLocal;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;

public final class JodaTimeContextHolder
{
    private static final ThreadLocal<JodaTimeContext> jodaTimeContextHolder;
    
    public static void resetJodaTimeContext() {
        JodaTimeContextHolder.jodaTimeContextHolder.remove();
    }
    
    public static void setJodaTimeContext(final JodaTimeContext jodaTimeContext) {
        if (jodaTimeContext == null) {
            resetJodaTimeContext();
        }
        else {
            JodaTimeContextHolder.jodaTimeContextHolder.set(jodaTimeContext);
        }
    }
    
    public static JodaTimeContext getJodaTimeContext() {
        return JodaTimeContextHolder.jodaTimeContextHolder.get();
    }
    
    public static DateTimeFormatter getFormatter(final DateTimeFormatter formatter, final Locale locale) {
        final DateTimeFormatter formatterToUse = (locale != null) ? formatter.withLocale(locale) : formatter;
        final JodaTimeContext context = getJodaTimeContext();
        return (context != null) ? context.getFormatter(formatterToUse) : formatterToUse;
    }
    
    static {
        jodaTimeContextHolder = new NamedThreadLocal<JodaTimeContext>("JodaTime Context");
    }
}
