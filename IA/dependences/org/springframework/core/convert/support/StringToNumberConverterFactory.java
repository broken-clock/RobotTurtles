// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.NumberUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

final class StringToNumberConverterFactory implements ConverterFactory<String, Number>
{
    @Override
    public <T extends Number> Converter<String, T> getConverter(final Class<T> targetType) {
        return new StringToNumber<T>(targetType);
    }
    
    private static final class StringToNumber<T extends Number> implements Converter<String, T>
    {
        private final Class<T> targetType;
        
        public StringToNumber(final Class<T> targetType) {
            this.targetType = targetType;
        }
        
        @Override
        public T convert(final String source) {
            if (source.length() == 0) {
                return null;
            }
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }
}
