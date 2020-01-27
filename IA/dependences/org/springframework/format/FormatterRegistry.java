// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format;

import java.lang.annotation.Annotation;
import org.springframework.core.convert.converter.ConverterRegistry;

public interface FormatterRegistry extends ConverterRegistry
{
    void addFormatter(final Formatter<?> p0);
    
    void addFormatterForFieldType(final Class<?> p0, final Formatter<?> p1);
    
    void addFormatterForFieldType(final Class<?> p0, final Printer<?> p1, final Parser<?> p2);
    
    void addFormatterForFieldAnnotation(final AnnotationFormatterFactory<? extends Annotation> p0);
}
