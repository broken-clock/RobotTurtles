// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Arrays;
import org.springframework.util.ObjectUtils;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ArrayToStringConverter implements ConditionalGenericConverter
{
    private final CollectionToStringConverter helperConverter;
    
    public ArrayToStringConverter(final ConversionService conversionService) {
        this.helperConverter = new CollectionToStringConverter(conversionService);
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, String.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.helperConverter.matches(sourceType, targetType);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.helperConverter.convert(Arrays.asList(ObjectUtils.toObjectArray(source)), sourceType, targetType);
    }
}
