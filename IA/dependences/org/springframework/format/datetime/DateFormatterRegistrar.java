// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import java.util.Calendar;
import org.springframework.format.Formatter;
import java.lang.annotation.Annotation;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.Assert;
import org.springframework.format.FormatterRegistrar;

public class DateFormatterRegistrar implements FormatterRegistrar
{
    private DateFormatter dateFormatter;
    
    public void setFormatter(final DateFormatter dateFormatter) {
        Assert.notNull(dateFormatter, "DateFormatter must not be null");
        this.dateFormatter = dateFormatter;
    }
    
    @Override
    public void registerFormatters(final FormatterRegistry registry) {
        addDateConverters(registry);
        registry.addFormatterForFieldAnnotation(new DateTimeFormatAnnotationFormatterFactory());
        if (this.dateFormatter != null) {
            registry.addFormatter(this.dateFormatter);
            registry.addFormatterForFieldType(Calendar.class, this.dateFormatter);
        }
    }
    
    public static void addDateConverters(final ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new DateToLongConverter());
        converterRegistry.addConverter(new DateToCalendarConverter());
        converterRegistry.addConverter(new CalendarToDateConverter());
        converterRegistry.addConverter(new CalendarToLongConverter());
        converterRegistry.addConverter(new LongToDateConverter());
        converterRegistry.addConverter(new LongToCalendarConverter());
    }
    
    private static class DateToLongConverter implements Converter<Date, Long>
    {
        @Override
        public Long convert(final Date source) {
            return source.getTime();
        }
    }
    
    private static class DateToCalendarConverter implements Converter<Date, Calendar>
    {
        @Override
        public Calendar convert(final Date source) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(source);
            return calendar;
        }
    }
    
    private static class CalendarToDateConverter implements Converter<Calendar, Date>
    {
        @Override
        public Date convert(final Calendar source) {
            return source.getTime();
        }
    }
    
    private static class CalendarToLongConverter implements Converter<Calendar, Long>
    {
        @Override
        public Long convert(final Calendar source) {
            return source.getTimeInMillis();
        }
    }
    
    private static class LongToDateConverter implements Converter<Long, Date>
    {
        @Override
        public Date convert(final Long source) {
            return new Date(source);
        }
    }
    
    private static class LongToCalendarConverter implements Converter<Long, Calendar>
    {
        @Override
        public Calendar convert(final Long source) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(source);
            return calendar;
        }
    }
}
