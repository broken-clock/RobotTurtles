// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.converter;

public interface ConverterFactory<S, R>
{
     <T extends R> Converter<S, T> getConverter(final Class<T> p0);
}
