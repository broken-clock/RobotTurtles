// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.jrockit;

import org.springframework.objenesis.ObjenesisException;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class JRockitLegacyInstantiator<T> implements ObjectInstantiator<T>
{
    private static Method safeAllocObjectMethod;
    private final Class<T> type;
    
    private static void initialize() {
        if (JRockitLegacyInstantiator.safeAllocObjectMethod == null) {
            try {
                final Class<?> memSystem = Class.forName("jrockit.vm.MemSystem");
                (JRockitLegacyInstantiator.safeAllocObjectMethod = memSystem.getDeclaredMethod("safeAllocObject", Class.class)).setAccessible(true);
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (ClassNotFoundException e2) {
                throw new ObjenesisException(e2);
            }
            catch (NoSuchMethodException e3) {
                throw new ObjenesisException(e3);
            }
        }
    }
    
    public JRockitLegacyInstantiator(final Class<T> type) {
        initialize();
        this.type = type;
    }
    
    public T newInstance() {
        try {
            return this.type.cast(JRockitLegacyInstantiator.safeAllocObjectMethod.invoke(null, this.type));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
    
    static {
        JRockitLegacyInstantiator.safeAllocObjectMethod = null;
    }
}
