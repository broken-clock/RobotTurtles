// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import org.joda.time.ReadableInstant;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.core.convert.converter.ConverterRegistry;

final class JodaTimeConverters
{
    public static void registerConverters(final ConverterRegistry registry) {
        DateFormatterRegistrar.addDateConverters(registry);
        registry.addConverter(new DateTimeToLocalDateConverter());
        registry.addConverter(new DateTimeToLocalTimeConverter());
        registry.addConverter(new DateTimeToLocalDateTimeConverter());
        registry.addConverter(new DateTimeToDateMidnightConverter());
        registry.addConverter(new DateTimeToMutableDateTimeConverter());
        registry.addConverter(new DateTimeToInstantConverter());
        registry.addConverter(new DateTimeToDateConverter());
        registry.addConverter(new DateTimeToCalendarConverter());
        registry.addConverter(new DateTimeToLongConverter());
        registry.addConverter(new DateToReadableInstantConverter());
        registry.addConverter(new CalendarToReadableInstantConverter());
        registry.addConverter(new LongToReadableInstantConverter());
        registry.addConverter(new LocalDateTimeToLocalDateConverter());
        registry.addConverter(new LocalDateTimeToLocalTimeConverter());
    }
    
    private static class DateTimeToLocalDateConverter implements Converter<DateTime, LocalDate>
    {
        @Override
        public LocalDate convert(final DateTime source) {
            return source.toLocalDate();
        }
    }
    
    private static class DateTimeToLocalTimeConverter implements Converter<DateTime, LocalTime>
    {
        @Override
        public LocalTime convert(final DateTime source) {
            return source.toLocalTime();
        }
    }
    
    private static class DateTimeToLocalDateTimeConverter implements Converter<DateTime, LocalDateTime>
    {
        @Override
        public LocalDateTime convert(final DateTime source) {
            return source.toLocalDateTime();
        }
    }
    
    @Deprecated
    private static class DateTimeToDateMidnightConverter implements Converter<DateTime, DateMidnight>
    {
        @Override
        public DateMidnight convert(final DateTime source) {
            return source.toDateMidnight();
        }
    }
    
    private static class DateTimeToMutableDateTimeConverter implements Converter<DateTime, MutableDateTime>
    {
        @Override
        public MutableDateTime convert(final DateTime source) {
            return source.toMutableDateTime();
        }
    }
    
    private static class DateTimeToInstantConverter implements Converter<DateTime, Instant>
    {
        @Override
        public Instant convert(final DateTime source) {
            return source.toInstant();
        }
    }
    
    private static class DateTimeToDateConverter implements Converter<DateTime, Date>
    {
        @Override
        public Date convert(final DateTime source) {
            return source.toDate();
        }
    }
    
    private static class DateTimeToCalendarConverter implements Converter<DateTime, Calendar>
    {
        @Override
        public Calendar convert(final DateTime source) {
            return source.toGregorianCalendar();
        }
    }
    
    private static class DateTimeToLongConverter implements Converter<DateTime, Long>
    {
        @Override
        public Long convert(final DateTime source) {
            return source.getMillis();
        }
    }
    
    private static class DateToReadableInstantConverter implements Converter<Date, ReadableInstant>
    {
        @Override
        public ReadableInstant convert(final Date source) {
            return (ReadableInstant)new DateTime((Object)source);
        }
    }
    
    private static class CalendarToReadableInstantConverter implements Converter<Calendar, ReadableInstant>
    {
        @Override
        public ReadableInstant convert(final Calendar source) {
            return (ReadableInstant)new DateTime((Object)source);
        }
    }
    
    private static class LongToReadableInstantConverter implements Converter<Long, ReadableInstant>
    {
        @Override
        public ReadableInstant convert(final Long source) {
            return (ReadableInstant)new DateTime((long)source);
        }
    }
    
    private static class LocalDateTimeToLocalDateConverter implements Converter<LocalDateTime, LocalDate>
    {
        @Override
        public LocalDate convert(final LocalDateTime source) {
            return source.toLocalDate();
        }
    }
    
    private static class LocalDateTimeToLocalTimeConverter implements Converter<LocalDateTime, LocalTime>
    {
        @Override
        public LocalTime convert(final LocalDateTime source) {
            return source.toLocalTime();
        }
    }
}
