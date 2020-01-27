// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Collection;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ObjectToCollectionConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public ObjectToCollectionConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Collection.class));
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
        final Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), 1);
        if (targetType.getElementTypeDescriptor() == null || targetType.getElementTypeDescriptor().isCollection()) {
            target.add(source);
        }
        else {
            final Object singleElement = this.conversionService.convert(source, sourceType, targetType.getElementTypeDescriptor());
            target.add(singleElement);
        }
        return target;
    }
}
