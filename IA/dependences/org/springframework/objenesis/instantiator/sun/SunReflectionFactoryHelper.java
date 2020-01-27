// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.sun;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Constructor;

class SunReflectionFactoryHelper
{
    public static <T> Constructor<T> newConstructorForSerialization(final Class<T> type, final Constructor<?> constructor) {
        final Class<?> reflectionFactoryClass = getReflectionFactoryClass();
        final Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);
        final Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(reflectionFactoryClass);
        try {
            return (Constructor<T>)newConstructorForSerializationMethod.invoke(reflectionFactory, type, constructor);
        }
        catch (IllegalArgumentException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e2) {
            throw new ObjenesisException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
    }
    
    private static Class<?> getReflectionFactoryClass() {
        try {
            return Class.forName("sun.reflect.ReflectionFactory");
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
    }
    
    private static Object createReflectionFactory(final Class<?> reflectionFactoryClass) {
        try {
            final Method method = reflectionFactoryClass.getDeclaredMethod("getReflectionFactory", (Class<?>[])new Class[0]);
            return method.invoke(null, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e2) {
            throw new ObjenesisException(e2);
        }
        catch (IllegalArgumentException e3) {
            throw new ObjenesisException(e3);
        }
        catch (InvocationTargetException e4) {
            throw new ObjenesisException(e4);
        }
    }
    
    private static Method getNewConstructorForSerializationMethod(final Class<?> reflectionFactoryClass) {
        try {
            return reflectionFactoryClass.getDeclaredMethod("newConstructorForSerialization", Class.class, Constructor.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}
