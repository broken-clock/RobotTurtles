// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Iterator;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import java.util.Set;

public abstract class ConversionServiceFactory
{
    public static void registerConverters(final Set<?> converters, final ConverterRegistry registry) {
        if (converters != null) {
            for (final Object converter : converters) {
                if (converter instanceof GenericConverter) {
                    registry.addConverter((GenericConverter)converter);
                }
                else if (converter instanceof Converter) {
                    registry.addConverter((Converter<?, ?>)converter);
                }
                else {
                    if (!(converter instanceof ConverterFactory)) {
                        throw new IllegalArgumentException("Each converter object must implement one of the Converter, ConverterFactory, or GenericConverter interfaces");
                    }
                    registry.addConverterFactory((ConverterFactory<?, ?>)converter);
                }
            }
        }
    }
    
    @Deprecated
    public static GenericConversionService createDefaultConversionService() {
        return new DefaultConversionService();
    }
    
    @Deprecated
    public static void addDefaultConverters(final GenericConversionService conversionService) {
        DefaultConversionService.addDefaultConverters(conversionService);
    }
}
