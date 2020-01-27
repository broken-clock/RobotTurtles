// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.lang.annotation.Annotation;
import org.springframework.format.AnnotationFormatterFactory;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.ReadableInstant;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.HashMap;
import org.joda.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.format.FormatterRegistrar;

public class JodaTimeFormatterRegistrar implements FormatterRegistrar
{
    private final Map<Type, DateTimeFormatter> formatters;
    private final Map<Type, DateTimeFormatterFactory> factories;
    
    public JodaTimeFormatterRegistrar() {
        this.formatters = new HashMap<Type, DateTimeFormatter>();
        this.factories = new HashMap<Type, DateTimeFormatterFactory>();
        for (final Type type : Type.values()) {
            this.factories.put(type, new DateTimeFormatterFactory());
        }
    }
    
    public void setUseIsoFormat(final boolean useIsoFormat) {
        this.factories.get(Type.DATE).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE : null);
        this.factories.get(Type.TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.TIME : null);
        this.factories.get(Type.DATE_TIME).setIso(useIsoFormat ? DateTimeFormat.ISO.DATE_TIME : null);
    }
    
    public void setDateStyle(final String dateStyle) {
        this.factories.get(Type.DATE).setStyle(dateStyle + "-");
    }
    
    public void setTimeStyle(final String timeStyle) {
        this.factories.get(Type.TIME).setStyle("-" + timeStyle);
    }
    
    public void setDateTimeStyle(final String dateTimeStyle) {
        this.factories.get(Type.DATE_TIME).setStyle(dateTimeStyle);
    }
    
    public void setDateFormatter(final DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE, formatter);
    }
    
    public void setTimeFormatter(final DateTimeFormatter formatter) {
        this.formatters.put(Type.TIME, formatter);
    }
    
    public void setDateTimeFormatter(final DateTimeFormatter formatter) {
        this.formatters.put(Type.DATE_TIME, formatter);
    }
    
    @Override
    public void registerFormatters(final FormatterRegistry registry) {
        JodaTimeConverters.registerConverters(registry);
        final DateTimeFormatter dateFormatter = this.getFormatter(Type.DATE);
        final DateTimeFormatter timeFormatter = this.getFormatter(Type.TIME);
        final DateTimeFormatter dateTimeFormatter = this.getFormatter(Type.DATE_TIME);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(dateFormatter), new LocalDateParser(dateFormatter), LocalDate.class);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(timeFormatter), new LocalTimeParser(timeFormatter), LocalTime.class);
        this.addFormatterForFields(registry, new ReadablePartialPrinter(dateTimeFormatter), new LocalDateTimeParser(dateTimeFormatter), LocalDateTime.class);
        this.addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), ReadableInstant.class);
        if (this.formatters.containsKey(Type.DATE_TIME)) {
            this.addFormatterForFields(registry, new ReadableInstantPrinter(dateTimeFormatter), new DateTimeParser(dateTimeFormatter), Date.class, Calendar.class);
        }
        registry.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
    }
    
    private DateTimeFormatter getFormatter(final Type type) {
        final DateTimeFormatter formatter = this.formatters.get(type);
        if (formatter != null) {
            return formatter;
        }
        final DateTimeFormatter fallbackFormatter = this.getFallbackFormatter(type);
        return this.factories.get(type).createDateTimeFormatter(fallbackFormatter);
    }
    
    private DateTimeFormatter getFallbackFormatter(final Type type) {
        switch (type) {
            case DATE: {
                return org.joda.time.format.DateTimeFormat.shortDate();
            }
            case TIME: {
                return org.joda.time.format.DateTimeFormat.shortTime();
            }
            default: {
                return org.joda.time.format.DateTimeFormat.shortDateTime();
            }
        }
    }
    
    private void addFormatterForFields(final FormatterRegistry registry, final Printer<?> printer, final Parser<?> parser, final Class<?>... fieldTypes) {
        for (final Class<?> fieldType : fieldTypes) {
            registry.addFormatterForFieldType(fieldType, printer, parser);
        }
    }
    
    private enum Type
    {
        DATE, 
        TIME, 
        DATE_TIME;
    }
}
