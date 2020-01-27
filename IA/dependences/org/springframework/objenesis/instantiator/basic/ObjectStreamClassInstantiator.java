// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class ObjectStreamClassInstantiator<T> implements ObjectInstantiator<T>
{
    private static Method newInstanceMethod;
    private final ObjectStreamClass objStreamClass;
    
    private static void initialize() {
        if (ObjectStreamClassInstantiator.newInstanceMethod == null) {
            try {
                (ObjectStreamClassInstantiator.newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", (Class<?>[])new Class[0])).setAccessible(true);
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e2) {
                throw new ObjenesisException(e2);
            }
        }
    }
    
    public ObjectStreamClassInstantiator(final Class<T> type) {
        initialize();
        this.objStreamClass = ObjectStreamClass.lookup(type);
    }
    
    public T newInstance() {
        try {
            return (T)ObjectStreamClassInstantiator.newInstanceMethod.invoke(this.objStreamClass, new Object[0]);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
