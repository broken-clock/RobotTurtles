// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.ClassUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

final class EnumToStringConverter implements Converter<Enum<?>, String>, ConditionalConverter
{
    private final ConversionService conversionService;
    
    public EnumToStringConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        for (final Class<?> interfaceType : ClassUtils.getAllInterfacesForClass(sourceType.getType())) {
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(interfaceType), targetType)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String convert(final Enum<?> source) {
        return source.name();
    }
}
