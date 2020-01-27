// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.ClassUtils;
import java.lang.reflect.Modifier;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class IdToEntityConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public IdToEntityConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        final Method finder = this.getFinder(targetType.getType());
        return finder != null && this.conversionService.canConvert(sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final Method finder = this.getFinder(targetType.getType());
        final Object id = this.conversionService.convert(source, sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
        return ReflectionUtils.invokeMethod(finder, source, id);
    }
    
    private Method getFinder(final Class<?> entityClass) {
        final String finderMethod = "find" + this.getEntityName(entityClass);
        final Method[] declaredMethods;
        final Method[] methods = declaredMethods = entityClass.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1 && method.getReturnType().equals(entityClass) && method.getName().equals(finderMethod)) {
                return method;
            }
        }
        return null;
    }
    
    private String getEntityName(final Class<?> entityClass) {
        final String shortName = ClassUtils.getShortName(entityClass);
        final int lastDot = shortName.lastIndexOf(46);
        if (lastDot != -1) {
            return shortName.substring(lastDot + 1);
        }
        return shortName;
    }
}
