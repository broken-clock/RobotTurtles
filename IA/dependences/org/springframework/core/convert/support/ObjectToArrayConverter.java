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

final class ObjectToArrayConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public ObjectToArrayConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object[].class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), 1);
        final Object targetElement = this.conversionService.convert(source, sourceType, targetType.getElementTypeDescriptor());
        Array.set(target, 0, targetElement);
        return target;
    }
}
