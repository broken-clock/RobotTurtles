// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.util.Collections;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import java.time.temporal.TemporalAccessor;
import org.springframework.format.Parser;
import java.time.format.DateTimeFormatter;
import org.springframework.format.Printer;
import org.springframework.util.StringValueResolver;
import java.util.Set;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.AnnotationFormatterFactory;

public class Jsr310DateTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<DateTimeFormat>, EmbeddedValueResolverAware
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
        return Jsr310DateTimeFormatAnnotationFormatterFactory.FIELD_TYPES;
    }
    
    @Override
    public Printer<?> getPrinter(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateTimeFormatter formatter = this.getFormatter(annotation, fieldType);
        return new TemporalAccessorPrinter(formatter);
    }
    
    @Override
    public Parser<?> getParser(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateTimeFormatter formatter = this.getFormatter(annotation, fieldType);
        return new TemporalAccessorParser((Class<? extends TemporalAccessor>)fieldType, formatter);
    }
    
    protected DateTimeFormatter getFormatter(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        factory.setStylePattern(this.resolveEmbeddedValue(annotation.style()));
        factory.setIso(annotation.iso());
        factory.setPattern(this.resolveEmbeddedValue(annotation.pattern()));
        return factory.createDateTimeFormatter();
    }
    
    static {
        final Set<Class<?>> fieldTypes = new HashSet<Class<?>>(8);
        fieldTypes.add(LocalDate.class);
        fieldTypes.add(LocalTime.class);
        fieldTypes.add(LocalDateTime.class);
        fieldTypes.add(ZonedDateTime.class);
        fieldTypes.add(OffsetDateTime.class);
        fieldTypes.add(OffsetTime.class);
        FIELD_TYPES = Collections.unmodifiableSet((Set<? extends Class<?>>)fieldTypes);
    }
}
