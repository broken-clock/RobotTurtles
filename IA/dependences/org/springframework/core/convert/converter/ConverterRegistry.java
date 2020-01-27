// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.converter;

public interface ConverterRegistry
{
    void addConverter(final Converter<?, ?> p0);
    
    void addConverter(final Class<?> p0, final Class<?> p1, final Converter<?, ?> p2);
    
    void addConverter(final GenericConverter p0);
    
    void addConverterFactory(final ConverterFactory<?, ?> p0);
    
    void removeConvertible(final Class<?> p0, final Class<?> p1);
}
