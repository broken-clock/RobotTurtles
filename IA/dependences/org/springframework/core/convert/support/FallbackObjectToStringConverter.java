// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.ClassUtils;
import java.io.StringWriter;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class FallbackObjectToStringConverter implements ConditionalGenericConverter
{
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, String.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        final Class<?> sourceClass = sourceType.getObjectType();
        return !String.class.equals(sourceClass) && (CharSequence.class.isAssignableFrom(sourceClass) || StringWriter.class.isAssignableFrom(sourceClass) || ObjectToObjectConverter.getOfMethod(sourceClass, String.class) != null || ClassUtils.getConstructorIfAvailable(sourceClass, String.class) != null);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return (source != null) ? source.toString() : null;
    }
}
