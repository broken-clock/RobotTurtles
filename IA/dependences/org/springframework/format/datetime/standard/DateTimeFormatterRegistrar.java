// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.lang.annotation.Annotation;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import java.time.temporal.TemporalAccessor;
import java.time.LocalDate;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistry;
import java.time.format.FormatStyle;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.format.FormatterRegistrar;

public class DateTimeFormatterRegistrar implements FormatterRegistrar
{
    private final Map<Type, DateTimeFormatter> formatters;
    private final Map<Type, DateTimeFormatterFactory> factories;
    
    public DateTimeFormatterRegistrar() {
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
    
    public void setDateStyle(final FormatStyle dateStyle) {
        this.factories.get(Type.DATE).setDateStyle(dateStyle);
    }
    
    public void setTimeStyle(final FormatStyle timeStyle) {
        this.factories.get(Type.TIME).setTimeStyle(timeStyle);
    }
    
    public void setDateTimeStyle(final FormatStyle dateTimeStyle) {
        this.factories.get(Type.DATE_TIME).setDateTimeStyle(dateTimeStyle);
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
        DateTimeConverters.registerConverters(registry);
        final DateTimeFormatter dateFormatter = this.getFormatter(Type.DATE);
        final DateTimeFormatter timeFormatter = this.getFormatter(Type.TIME);
        final DateTimeFormatter dateTimeFormatter = this.getFormatter(Type.DATE_TIME);
        registry.addFormatterForFieldType(LocalDate.class, new TemporalAccessorPrinter(dateFormatter), new TemporalAccessorParser(LocalDate.class, dateFormatter));
        registry.addFormatterForFieldType(LocalTime.class, new TemporalAccessorPrinter(timeFormatter), new TemporalAccessorParser(LocalTime.class, timeFormatter));
        registry.addFormatterForFieldType(LocalDateTime.class, new TemporalAccessorPrinter(dateTimeFormatter), new TemporalAccessorParser(LocalDateTime.class, dateTimeFormatter));
        registry.addFormatterForFieldType(ZonedDateTime.class, new TemporalAccessorPrinter(dateTimeFormatter), new TemporalAccessorParser(ZonedDateTime.class, dateTimeFormatter));
        registry.addFormatterForFieldType(OffsetDateTime.class, new TemporalAccessorPrinter(dateTimeFormatter), new TemporalAccessorParser(OffsetDateTime.class, dateTimeFormatter));
        registry.addFormatterForFieldType(OffsetTime.class, new TemporalAccessorPrinter(timeFormatter), new TemporalAccessorParser(OffsetTime.class, timeFormatter));
        registry.addFormatterForFieldType(Instant.class, new InstantFormatter());
        registry.addFormatterForFieldAnnotation(new Jsr310DateTimeFormatAnnotationFormatterFactory());
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
                return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            }
            case TIME: {
                return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
            }
            default: {
                return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
            }
        }
    }
    
    private enum Type
    {
        DATE, 
        TIME, 
        DATE_TIME;
    }
}
