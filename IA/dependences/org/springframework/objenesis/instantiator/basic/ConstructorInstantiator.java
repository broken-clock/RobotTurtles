// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Constructor;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class ConstructorInstantiator<T> implements ObjectInstantiator<T>
{
    protected Constructor<T> constructor;
    
    public ConstructorInstantiator(final Class<T> type) {
        try {
            this.constructor = type.getDeclaredConstructor((Class<?>[])null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
    
    public T newInstance() {
        try {
            return this.constructor.newInstance((Object[])null);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
