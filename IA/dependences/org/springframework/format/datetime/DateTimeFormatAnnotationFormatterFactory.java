// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime;

import java.util.Collections;
import java.util.Calendar;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import java.util.Date;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.util.StringValueResolver;
import java.util.Set;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.AnnotationFormatterFactory;

public class DateTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<DateTimeFormat>, EmbeddedValueResolverAware
{
    private static final Set<Class<?>> FIELD_TYPES;
    private StringValueResolver embeddedValueResolver;
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    @Override
    public Set<Class<?>> getFieldTypes() {
        return DateTimeFormatAnnotationFormatterFactory.FIELD_TYPES;
    }
    
    @Override
    public Printer<?> getPrinter(final DateTimeFormat annotation, final Class<?> fieldType) {
        return this.getFormatter(annotation, fieldType);
    }
    
    @Override
    public Parser<?> getParser(final DateTimeFormat annotation, final Class<?> fieldType) {
        return this.getFormatter(annotation, fieldType);
    }
    
    protected Formatter<Date> getFormatter(final DateTimeFormat annotation, final Class<?> fieldType) {
        final DateFormatter formatter = new DateFormatter();
        formatter.setStylePattern(this.resolveEmbeddedValue(annotation.style()));
        formatter.setIso(annotation.iso());
        formatter.setPattern(this.resolveEmbeddedValue(annotation.pattern()));
        return formatter;
    }
    
    protected String resolveEmbeddedValue(final String value) {
        return (this.embeddedValueResolver != null) ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
    
    static {
        final Set<Class<?>> fieldTypes = new HashSet<Class<?>>(4);
        fieldTypes.add(Date.class);
        fieldTypes.add(Calendar.class);
        fieldTypes.add(Long.class);
        FIELD_TYPES = Collections.unmodifiableSet((Set<? extends Class<?>>)fieldTypes);
    }
}
