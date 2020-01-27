// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.strategy;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import java.lang.reflect.Constructor;

public class SingleInstantiatorStrategy implements InstantiatorStrategy
{
    private Constructor<?> constructor;
    
    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(final Class<T> instantiator) {
        try {
            this.constructor = instantiator.getConstructor(Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }
    
    public <T> ObjectInstantiator<T> newInstantiatorOf(final Class<T> type) {
        try {
            return (ObjectInstantiator<T>)this.constructor.newInstance(type);
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e2) {
            throw new ObjenesisException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
    }
}
