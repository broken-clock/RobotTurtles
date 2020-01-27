// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.NumberUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

final class CharacterToNumberFactory implements ConverterFactory<Character, Number>
{
    @Override
    public <T extends Number> Converter<Character, T> getConverter(final Class<T> targetType) {
        return new CharacterToNumber<T>(targetType);
    }
    
    private static final class CharacterToNumber<T extends Number> implements Converter<Character, T>
    {
        private final Class<T> targetType;
        
        public CharacterToNumber(final Class<T> targetType) {
            this.targetType = targetType;
        }
        
        @Override
        public T convert(final Character source) {
            return NumberUtils.convertNumberToTargetClass((short)(char)source, this.targetType);
        }
    }
}
