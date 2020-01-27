// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.springframework.format.Parser;

public final class TemporalAccessorParser implements Parser<TemporalAccessor>
{
    private final Class<? extends TemporalAccessor> temporalAccessorType;
    private final DateTimeFormatter formatter;
    
    public TemporalAccessorParser(final Class<? extends TemporalAccessor> temporalAccessorType, final DateTimeFormatter formatter) {
        this.temporalAccessorType = temporalAccessorType;
        this.formatter = formatter;
    }
    
    @Override
    public TemporalAccessor parse(final String text, final Locale locale) throws ParseException {
        final DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(this.formatter, locale);
        if (LocalDate.class.equals(this.temporalAccessorType)) {
            return LocalDate.parse(text, formatterToUse);
        }
        if (LocalTime.class.equals(this.temporalAccessorType)) {
            return LocalTime.parse(text, formatterToUse);
        }
        if (LocalDateTime.class.equals(this.temporalAccessorType)) {
            return LocalDateTime.parse(text, formatterToUse);
        }
        if (ZonedDateTime.class.equals(this.temporalAccessorType)) {
            return ZonedDateTime.parse(text, formatterToUse);
        }
        if (OffsetDateTime.class.equals(this.temporalAccessorType)) {
            return OffsetDateTime.parse(text, formatterToUse);
        }
        if (OffsetTime.class.equals(this.temporalAccessorType)) {
            return OffsetTime.parse(text, formatterToUse);
        }
        throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
    }
}
