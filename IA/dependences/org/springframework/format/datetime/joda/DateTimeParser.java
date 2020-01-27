// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTime;
import org.springframework.format.Parser;

public final class DateTimeParser implements Parser<DateTime>
{
    private final DateTimeFormatter formatter;
    
    public DateTimeParser(final DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public DateTime parse(final String text, final Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseDateTime(text);
    }
}
