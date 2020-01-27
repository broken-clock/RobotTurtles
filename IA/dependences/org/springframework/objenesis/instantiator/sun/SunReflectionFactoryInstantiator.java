// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.sun;

import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Constructor;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class SunReflectionFactoryInstantiator<T> implements ObjectInstantiator<T>
{
    private final Constructor<T> mungedConstructor;
    
    public SunReflectionFactoryInstantiator(final Class<T> type) {
        final Constructor<Object> javaLangObjectConstructor = getJavaLangObjectConstructor();
        (this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, javaLangObjectConstructor)).setAccessible(true);
    }
    
    public T newInstance() {
        try {
            return this.mungedConstructor.newInstance((Object[])null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
    
    private static Constructor<Object> getJavaLangObjectConstructor() {
        try {
            return Object.class.getConstructor((Class<?>[])null);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
}
