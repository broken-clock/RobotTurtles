// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.Assert;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

final class StringToEnumConverterFactory implements ConverterFactory<String, Enum>
{
    @Override
    public <T extends Enum> Converter<String, T> getConverter(final Class<T> targetType) {
        Class<?> enumType;
        for (enumType = targetType; enumType != null && !enumType.isEnum(); enumType = enumType.getSuperclass()) {}
        Assert.notNull(enumType, "The target type " + targetType.getName() + " does not refer to an enum");
        return new StringToEnum<T>((Class<T>)enumType);
    }
    
    private class StringToEnum<T extends Enum> implements Converter<String, T>
    {
        private final Class<T> enumType;
        
        public StringToEnum(final Class<T> enumType) {
            this.enumType = enumType;
        }
        
        @Override
        public T convert(final String source) {
            if (source.length() == 0) {
                return null;
            }
            return Enum.valueOf(this.enumType, source.trim());
        }
    }
}
