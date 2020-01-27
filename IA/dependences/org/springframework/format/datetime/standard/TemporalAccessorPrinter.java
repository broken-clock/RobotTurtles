// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.springframework.format.Printer;

public final class TemporalAccessorPrinter implements Printer<TemporalAccessor>
{
    private final DateTimeFormatter formatter;
    
    public TemporalAccessorPrinter(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public String print(final TemporalAccessor partial, final Locale locale) {
        return DateTimeContextHolder.getFormatter(this.formatter, locale).format(partial);
    }
}
