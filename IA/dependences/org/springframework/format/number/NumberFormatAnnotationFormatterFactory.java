// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.number;

import java.lang.annotation.Annotation;
import org.springframework.util.StringUtils;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import java.util.Collections;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashSet;
import org.springframework.util.StringValueResolver;
import java.util.Set;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.AnnotationFormatterFactory;

public class NumberFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<NumberFormat>, EmbeddedValueResolverAware
{
    private final Set<Class<?>> fieldTypes;
    private StringValueResolver embeddedValueResolver;
    
    public NumberFormatAnnotationFormatterFactory() {
        final Set<Class<?>> rawFieldTypes = new HashSet<Class<?>>(7);
        rawFieldTypes.add(Short.class);
        rawFieldTypes.add(Integer.class);
        rawFieldTypes.add(Long.class);
        rawFieldTypes.add(Float.class);
        rawFieldTypes.add(Double.class);
        rawFieldTypes.add(BigDecimal.class);
        rawFieldTypes.add(BigInteger.class);
        this.fieldTypes = Collections.unmodifiableSet((Set<? extends Class<?>>)rawFieldTypes);
    }
    
    @Override
    public final Set<Class<?>> getFieldTypes() {
        return this.fieldTypes;
    }
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    protected String resolveEmbeddedValue(final String value) {
        return (this.embeddedValueResolver != null) ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
    
    @Override
    public Printer<Number> getPrinter(final NumberFormat annotation, final Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }
    
    @Override
    public Parser<Number> getParser(final NumberFormat annotation, final Class<?> fieldType) {
        return this.configureFormatterFrom(annotation);
    }
    
    private Formatter<Number> configureFormatterFrom(final NumberFormat annotation) {
        if (StringUtils.hasLength(annotation.pattern())) {
            return new NumberFormatter(this.resolveEmbeddedValue(annotation.pattern()));
        }
        final NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.PERCENT) {
            return new PercentFormatter();
        }
        if (style == NumberFormat.Style.CURRENCY) {
            return new CurrencyFormatter();
        }
        return new NumberFormatter();
    }
}
