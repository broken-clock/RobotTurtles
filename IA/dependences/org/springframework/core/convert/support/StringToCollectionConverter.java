// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.CollectionFactory;
import org.springframework.util.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Collection;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class StringToCollectionConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public StringToCollectionConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Collection.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return targetType.getElementTypeDescriptor() == null || this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final String string = (String)source;
        final String[] fields = StringUtils.commaDelimitedListToStringArray(string);
        final Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), fields.length);
        if (targetType.getElementTypeDescriptor() == null) {
            for (final String field : fields) {
                target.add(field.trim());
            }
        }
        else {
            for (final String field : fields) {
                final Object targetElement = this.conversionService.convert(field.trim(), sourceType, targetType.getElementTypeDescriptor());
                target.add(targetElement);
            }
        }
        return target;
    }
}
