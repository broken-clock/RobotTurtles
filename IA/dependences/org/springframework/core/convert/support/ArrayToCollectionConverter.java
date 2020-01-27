// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.CollectionFactory;
import java.lang.reflect.Array;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Collection;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ArrayToCollectionConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public ArrayToCollectionConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Collection.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final int length = Array.getLength(source);
        final Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), length);
        if (targetType.getElementTypeDescriptor() == null) {
            for (int i = 0; i < length; ++i) {
                final Object sourceElement = Array.get(source, i);
                target.add(sourceElement);
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                final Object sourceElement = Array.get(source, i);
                final Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
                target.add(targetElement);
            }
        }
        return target;
    }
}
