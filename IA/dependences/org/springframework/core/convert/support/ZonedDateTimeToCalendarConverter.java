// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.time.ZonedDateTime;
import org.springframework.core.convert.converter.Converter;

final class ZonedDateTimeToCalendarConverter implements Converter<ZonedDateTime, Calendar>
{
    @Override
    public Calendar convert(final ZonedDateTime source) {
        return GregorianCalendar.from(source);
    }
}
