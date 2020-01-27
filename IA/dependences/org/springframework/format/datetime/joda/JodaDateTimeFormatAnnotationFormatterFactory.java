// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.LocalDate;
import org.springframework.format.Parser;
import org.joda.time.format.DateTimeFormatter;
import java.util.Calendar;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.springframework.format.Printer;
import org.springframework.util.StringValueResolver;
import java.util.Set;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.AnnotationFormatterFactory;

public class JodaDateTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<DateTimeFormat>, EmbeddedValueResolverAware
{
    private static final Set<Class<?>> FIELD_TYPES;
    private StringValueResolver embeddedValueResolver;
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    protected String resolveEmbeddedValue(final String value) {
        return (this.embeddedValueResolver != null) ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
    
    @Override
    public final Set<Class<?>> getFieldTypes() {
        return JodaDateTimeFormatAnnotationFormatterFactory.FIELD_TYPES;
    }
    
    @Override
    public Printer<?> getPrinter(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateTimeFormatter formatter = this.getFormatter(annotation, fieldType);
        if (ReadablePartial.class.isAssignableFrom(fieldType)) {
            return new ReadablePartialPrinter(formatter);
        }
        if (ReadableInstant.class.isAssignableFrom(fieldType) || Calendar.class.isAssignableFrom(fieldType)) {
            return new ReadableInstantPrinter(formatter);
        }
        return new MillisecondInstantPrinter(formatter);
    }
    
    @Override
    public Parser<?> getParser(final DateTimeFormat annotation, final Class<?> fieldType) {
        if (LocalDate.class.equals(fieldType)) {
            return new LocalDateParser(this.getFormatter(annotation, fieldType));
        }
        if (LocalTime.class.equals(fieldType)) {
            return new LocalTimeParser(this.getFormatter(annotation, fieldType));
        }
        if (LocalDateTime.class.equals(fieldType)) {
            return new LocalDateTimeParser(this.getFormatter(annotation, fieldType));
        }
        return new DateTimeParser(this.getFormatter(annotation, fieldType));
    }
    
    protected DateTimeFormatter getFormatter(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        factory.setStyle(this.resolveEmbeddedValue(annotation.style()));
        factory.setIso(annotation.iso());
        factory.setPattern(this.resolveEmbeddedValue(annotation.pattern()));
        return factory.createDateTimeFormatter();
    }
    
    static {
        final Set<Class<?>> fieldTypes = new HashSet<Class<?>>(8);
        fieldTypes.add(ReadableInstant.class);
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet((Set<? extends Class<?>>)fieldTypes);
    }
}
