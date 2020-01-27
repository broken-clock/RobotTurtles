// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalTime;
import org.springframework.format.Parser;

public final class LocalTimeParser implements Parser<LocalTime>
{
    private final DateTimeFormatter formatter;
    
    public LocalTimeParser(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public LocalTime parse(final String text, final Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalTime(text);
    }
}
