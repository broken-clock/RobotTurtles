// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.support;

import org.springframework.util.ClassUtils;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import java.lang.annotation.Annotation;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringValueResolver;

public class DefaultFormattingConversionService extends FormattingConversionService
{
    private static final boolean jsr310Present;
    private static final boolean jodaTimePresent;
    
    public DefaultFormattingConversionService() {
        this(null, true);
    }
    
    public DefaultFormattingConversionService(final boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }
    
    public DefaultFormattingConversionService(final StringValueResolver embeddedValueResolver, final boolean registerDefaultFormatters) {
        this.setEmbeddedValueResolver(embeddedValueResolver);
        DefaultConversionService.addDefaultConverters(this);
        if (registerDefaultFormatters) {
            addDefaultFormatters(this);
        }
    }
    
    public static void addDefaultFormatters(final FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (DefaultFormattingConversionService.jsr310Present) {
            new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        }
        if (DefaultFormattingConversionService.jodaTimePresent) {
            new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        }
        else {
            new DateFormatterRegistrar().registerFormatters(formatterRegistry);
        }
    }
    
    static {
        jsr310Present = ClassUtils.isPresent("java.time.LocalDate", DefaultFormattingConversionService.class.getClassLoader());
        jodaTimePresent = ClassUtils.isPresent("org.joda.time.LocalDate", DefaultFormattingConversionService.class.getClassLoader());
    }
}
