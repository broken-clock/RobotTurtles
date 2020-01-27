// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadablePartial;
import org.springframework.format.Printer;

public final class ReadablePartialPrinter implements Printer<ReadablePartial>
{
    private final DateTimeFormatter formatter;
    
    public ReadablePartialPrinter(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public String print(final ReadablePartial partial, final Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(partial);
    }
}
