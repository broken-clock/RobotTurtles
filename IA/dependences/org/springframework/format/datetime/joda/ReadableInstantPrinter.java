// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadableInstant;
import org.springframework.format.Printer;

public final class ReadableInstantPrinter implements Printer<ReadableInstant>
{
    private final DateTimeFormatter formatter;
    
    public ReadableInstantPrinter(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public String print(final ReadableInstant instant, final Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(instant);
    }
}
