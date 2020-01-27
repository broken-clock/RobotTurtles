// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Printer;

public final class MillisecondInstantPrinter implements Printer<Long>
{
    private final DateTimeFormatter formatter;
    
    public MillisecondInstantPrinter(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public String print(final Long instant, final Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print((long)instant);
    }
}
