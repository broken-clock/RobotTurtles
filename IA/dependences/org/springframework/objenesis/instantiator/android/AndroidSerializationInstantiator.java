// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.android;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Method;
import java.io.ObjectStreamClass;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class AndroidSerializationInstantiator<T> implements ObjectInstantiator<T>
{
    private final Class<T> type;
    private final ObjectStreamClass objectStreamClass;
    private final Method newInstanceMethod;
    
    public AndroidSerializationInstantiator(final Class<T> type) {
        this.type = type;
        this.newInstanceMethod = getNewInstanceMethod();
        Method m = null;
        try {
            m = ObjectStreamClass.class.getMethod("lookupAny", Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        try {
            this.objectStreamClass = (ObjectStreamClass)m.invoke(null, type);
        }
        catch (IllegalAccessException e2) {
            throw new ObjenesisException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
    }
    
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(this.objectStreamClass, this.type));
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalArgumentException e2) {
            throw new ObjenesisException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
    }
    
    private static Method getNewInstanceMethod() {
        try {
            final Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
    }
}
