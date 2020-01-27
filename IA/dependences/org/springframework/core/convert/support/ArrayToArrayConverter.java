// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.List;
import java.util.Arrays;
import org.springframework.util.ObjectUtils;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ArrayToArrayConverter implements ConditionalGenericConverter
{
    private final CollectionToArrayConverter helperConverter;
    private final ConversionService conversionService;
    
    public ArrayToArrayConverter(final ConversionService conversionService) {
        this.helperConverter = new CollectionToArrayConverter(conversionService);
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Object[].class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.helperConverter.matches(sourceType, targetType);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (this.conversionService instanceof GenericConversionService && ((GenericConversionService)this.conversionService).canBypassConvert(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor())) {
            return source;
        }
        final List<Object> sourceList = Arrays.asList(ObjectUtils.toObjectArray(source));
        return this.helperConverter.convert(sourceList, sourceType, targetType);
    }
}
