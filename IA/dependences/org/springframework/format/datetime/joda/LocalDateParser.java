// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;
import org.springframework.format.Parser;

public final class LocalDateParser implements Parser<LocalDate>
{
    private final DateTimeFormatter formatter;
    
    public LocalDateParser(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public LocalDate parse(final String text, final Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDate(text);
    }
}
