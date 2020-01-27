// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.sun;

import java.lang.reflect.Field;
import org.springframework.objenesis.ObjenesisException;
import sun.misc.Unsafe;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class UnsafeFactoryInstantiator<T> implements ObjectInstantiator<T>
{
    private static Unsafe unsafe;
    private final Class<T> type;
    
    public UnsafeFactoryInstantiator(final Class<T> type) {
        if (UnsafeFactoryInstantiator.unsafe == null) {
            Field f;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
            }
            catch (NoSuchFieldException e) {
                throw new ObjenesisException(e);
            }
            f.setAccessible(true);
            try {
                UnsafeFactoryInstantiator.unsafe = (Unsafe)f.get(null);
            }
            catch (IllegalAccessException e2) {
                throw new ObjenesisException(e2);
            }
        }
        this.type = type;
    }
    
    public T newInstance() {
        try {
            return this.type.cast(UnsafeFactoryInstantiator.unsafe.allocateInstance(this.type));
        }
        catch (InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}
