// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.sun;

import org.springframework.objenesis.ObjenesisException;
import java.io.NotSerializableException;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;
import java.lang.reflect.Constructor;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class SunReflectionFactorySerializationInstantiator<T> implements ObjectInstantiator<T>
{
    private final Constructor<T> mungedConstructor;
    
    public SunReflectionFactorySerializationInstantiator(final Class<T> type) {
        final Class<? super T> nonSerializableAncestor = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
        Constructor<? super T> nonSerializableAncestorConstructor;
        try {
            nonSerializableAncestorConstructor = nonSerializableAncestor.getConstructor((Class<?>[])null);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(new NotSerializableException(type + " has no suitable superclass constructor"));
        }
        (this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, nonSerializableAncestorConstructor)).setAccessible(true);
    }
    
    public T newInstance() {
        try {
            return this.mungedConstructor.newInstance((Object[])null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
