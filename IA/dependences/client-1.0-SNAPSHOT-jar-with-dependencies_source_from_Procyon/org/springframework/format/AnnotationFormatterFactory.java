// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format;

import java.util.Set;
import java.lang.annotation.Annotation;

public interface AnnotationFormatterFactory<A extends Annotation>
{
    Set<Class<?>> getFieldTypes();
    
    Printer<?> getPrinter(final A p0, final Class<?> p1);
    
    Parser<?> getParser(final A p0, final Class<?> p1);
}
