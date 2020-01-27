// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.lang.reflect.Array;
import org.springframework.util.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class StringToArrayConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public StringToArrayConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object[].class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final String string = (String)source;
        final String[] fields = StringUtils.commaDelimitedListToStringArray(string);
        final Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), fields.length);
        for (int i = 0; i < fields.length; ++i) {
            final String sourceElement = fields[i];
            final Object targetElement = this.conversionService.convert(sourceElement.trim(), sourceType, targetType.getElementTypeDescriptor());
            Array.set(target, i, targetElement);
        }
        return target;
    }
}
