// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ClassUtils;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ObjectToObjectConverter implements ConditionalGenericConverter
{
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return !sourceType.getType().equals(targetType.getType()) && (String.class.equals(targetType.getType()) ? (ClassUtils.getConstructorIfAvailable(String.class, sourceType.getType()) != null) : hasToMethodOrOfMethodOrConstructor(targetType.getType(), sourceType.getType()));
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        final Class<?> sourceClass = sourceType.getType();
        final Class<?> targetClass = targetType.getType();
        try {
            if (!String.class.equals(targetClass)) {
                Method method = getToMethod(targetClass, sourceClass);
                if (method != null) {
                    ReflectionUtils.makeAccessible(method);
                    return method.invoke(source, new Object[0]);
                }
                method = getOfMethod(targetClass, sourceClass);
                if (method != null) {
                    ReflectionUtils.makeAccessible(method);
                    return method.invoke(null, source);
                }
            }
            final Constructor<?> constructor = ClassUtils.getConstructorIfAvailable(targetClass, sourceClass);
            if (constructor != null) {
                return constructor.newInstance(source);
            }
        }
        catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        }
        catch (Throwable ex2) {
            throw new ConversionFailedException(sourceType, targetType, source, ex2);
        }
        throw new IllegalStateException("No static valueOf/of/from(" + sourceClass.getName() + ") method or Constructor(" + sourceClass.getName() + ") exists on " + targetClass.getName());
    }
    
    private static boolean hasToMethodOrOfMethodOrConstructor(final Class<?> targetClass, final Class<?> sourceClass) {
        return getToMethod(targetClass, sourceClass) != null || getOfMethod(targetClass, sourceClass) != null || ClassUtils.getConstructorIfAvailable(targetClass, sourceClass) != null;
    }
    
    private static Method getToMethod(final Class<?> targetClass, final Class<?> sourceClass) {
        final Method method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName(), (Class<?>[])new Class[0]);
        return (method != null && targetClass.equals(method.getReturnType())) ? method : null;
    }
    
    static Method getOfMethod(final Class<?> targetClass, final Class<?> sourceClass) {
        Method method = ClassUtils.getStaticMethod(targetClass, "valueOf", sourceClass);
        if (method == null) {
            method = ClassUtils.getStaticMethod(targetClass, "of", sourceClass);
            if (method == null) {
                method = ClassUtils.getStaticMethod(targetClass, "from", sourceClass);
            }
        }
        return method;
    }
}
