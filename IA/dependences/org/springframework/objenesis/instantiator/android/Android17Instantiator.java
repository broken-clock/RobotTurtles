// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.android;

import java.lang.reflect.InvocationTargetException;
import java.io.ObjectStreamClass;
import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class Android17Instantiator<T> implements ObjectInstantiator<T>
{
    private final Class<T> type;
    private final Method newInstanceMethod;
    private final Integer objectConstructorId;
    
    public Android17Instantiator(final Class<T> type) {
        this.type = type;
        this.newInstanceMethod = getNewInstanceMethod();
        this.objectConstructorId = findConstructorIdForJavaLangObjectConstructor();
    }
    
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(null, this.type, this.objectConstructorId));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
    
    private static Method getNewInstanceMethod() {
        try {
            final Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
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
    
    private static Integer findConstructorIdForJavaLangObjectConstructor() {
        try {
            final Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            newInstanceMethod.setAccessible(true);
            return (Integer)newInstanceMethod.invoke(null, Object.class);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
        catch (IllegalAccessException e3) {
            throw new ObjenesisException(e3);
        }
        catch (InvocationTargetException e4) {
            throw new ObjenesisException(e4);
        }
    }
}
