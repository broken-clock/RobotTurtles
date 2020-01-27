// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.NumberUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.ConverterFactory;

final class NumberToNumberConverterFactory implements ConverterFactory<Number, Number>, ConditionalConverter
{
    @Override
    public <T extends Number> Converter<Number, T> getConverter(final Class<T> targetType) {
        return new NumberToNumber<T>(targetType);
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return !sourceType.equals(targetType);
    }
    
    private static final class NumberToNumber<T extends Number> implements Converter<Number, T>
    {
        private final Class<T> targetType;
        
        public NumberToNumber(final Class<T> targetType) {
            this.targetType = targetType;
        }
        
        @Override
        public T convert(final Number source) {
            return NumberUtils.convertNumberToTargetClass(source, this.targetType);
        }
    }
}
