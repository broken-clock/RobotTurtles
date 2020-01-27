// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Iterator;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Collection;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class CollectionToStringConverter implements ConditionalGenericConverter
{
    private static final String DELIMITER = ",";
    private final ConversionService conversionService;
    
    public CollectionToStringConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, String.class));
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
        final Collection<?> sourceCollection = (Collection<?>)source;
        if (sourceCollection.size() == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final Object sourceElement : sourceCollection) {
            if (i > 0) {
                sb.append(",");
            }
            final Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType);
            sb.append(targetElement);
            ++i;
        }
        return sb.toString();
    }
}
