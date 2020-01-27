// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.lang.reflect.Array;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ArrayToObjectConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public ArrayToObjectConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Object.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType, this.conversionService);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        if (sourceType.isAssignableTo(targetType)) {
            return source;
        }
        if (Array.getLength(source) == 0) {
            return null;
        }
        final Object firstElement = Array.get(source, 0);
        return this.conversionService.convert(firstElement, sourceType.elementTypeDescriptor(firstElement), targetType);
    }
}
