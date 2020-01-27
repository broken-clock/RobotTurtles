// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Iterator;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Collection;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class CollectionToCollectionConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public CollectionToCollectionConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, Collection.class));
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
        boolean copyRequired = !targetType.getType().isInstance(source);
        final Collection<?> sourceCollection = (Collection<?>)source;
        if (!copyRequired && sourceCollection.isEmpty()) {
            return sourceCollection;
        }
        final Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), sourceCollection.size());
        if (targetType.getElementTypeDescriptor() == null) {
            for (final Object element : sourceCollection) {
                target.add(element);
            }
        }
        else {
            for (final Object sourceElement : sourceCollection) {
                final Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
                target.add(targetElement);
                if (sourceElement != targetElement) {
                    copyRequired = true;
                }
            }
        }
        return copyRequired ? target : source;
    }
}
