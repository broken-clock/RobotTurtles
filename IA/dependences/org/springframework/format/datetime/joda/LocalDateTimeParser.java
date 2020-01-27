// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDateTime;
import org.springframework.format.Parser;

public final class LocalDateTimeParser implements Parser<LocalDateTime>
{
    private final DateTimeFormatter formatter;
    
    public LocalDateTimeParser(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public LocalDateTime parse(final String text, final Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDateTime(text);
    }
}
