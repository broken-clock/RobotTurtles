// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.android;

import java.io.ObjectInputStream;
import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class Android10Instantiator<T> implements ObjectInstantiator<T>
{
    private final Class<T> type;
    private final Method newStaticMethod;
    
    public Android10Instantiator(final Class<T> type) {
        this.type = type;
        this.newStaticMethod = getNewStaticMethod();
    }
    
    public T newInstance() {
        try {
            return this.type.cast(this.newStaticMethod.invoke(null, this.type, Object.class));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
    
    private static Method getNewStaticMethod() {
        try {
            final Method newStaticMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
            newStaticMethod.setAccessible(true);
            return newStaticMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
    }
}
