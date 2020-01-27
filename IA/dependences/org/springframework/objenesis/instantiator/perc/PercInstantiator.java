// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.perc;

import org.springframework.objenesis.ObjenesisException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class PercInstantiator<T> implements ObjectInstantiator<T>
{
    private final Method newInstanceMethod;
    private final Object[] typeArgs;
    
    public PercInstantiator(final Class<T> type) {
        (this.typeArgs = new Object[] { null, Boolean.FALSE })[0] = type;
        try {
            (this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Boolean.TYPE)).setAccessible(true);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
    }
    
    public T newInstance() {
        try {
            return (T)this.newInstanceMethod.invoke(null, this.typeArgs);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
