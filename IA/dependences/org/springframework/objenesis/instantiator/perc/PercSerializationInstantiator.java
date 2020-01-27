// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.perc;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class PercSerializationInstantiator<T> implements ObjectInstantiator<T>
{
    private Object[] typeArgs;
    private final Method newInstanceMethod;
    
    public PercSerializationInstantiator(final Class<T> type) {
        Class<? super T> unserializableType;
        for (unserializableType = type; Serializable.class.isAssignableFrom(unserializableType); unserializableType = unserializableType.getSuperclass()) {}
        try {
            final Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");
            (this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct", Class.class, Object.class, percMethodClass)).setAccessible(true);
            final Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
            final Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", Class.class);
            final Object someObject = getPercClassMethod.invoke(null, unserializableType);
            final Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod", String.class);
            final Object percMethod = findMethodMethod.invoke(someObject, "<init>()V");
            this.typeArgs = new Object[] { unserializableType, type, percMethod };
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new ObjenesisException(e4);
        }
    }
    
    public T newInstance() {
        try {
            return (T)this.newInstanceMethod.invoke(null, this.typeArgs);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e2) {
            throw new ObjenesisException(e2);
        }
    }
}
