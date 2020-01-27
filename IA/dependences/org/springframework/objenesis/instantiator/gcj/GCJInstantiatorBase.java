// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.gcj;

import java.io.IOException;
import org.springframework.objenesis.ObjenesisException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public abstract class GCJInstantiatorBase<T> implements ObjectInstantiator<T>
{
    static Method newObjectMethod;
    static ObjectInputStream dummyStream;
    protected final Class<T> type;
    
    private static void initialize() {
        if (GCJInstantiatorBase.newObjectMethod == null) {
            try {
                (GCJInstantiatorBase.newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", Class.class, Class.class)).setAccessible(true);
                GCJInstantiatorBase.dummyStream = new DummyStream();
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e2) {
                throw new ObjenesisException(e2);
            }
            catch (IOException e3) {
                throw new ObjenesisException(e3);
            }
        }
    }
    
    public GCJInstantiatorBase(final Class<T> type) {
        this.type = type;
        initialize();
    }
    
    public abstract T newInstance();
    
    static {
        GCJInstantiatorBase.newObjectMethod = null;
    }
    
    private static class DummyStream extends ObjectInputStream
    {
        public DummyStream() throws IOException {
        }
    }
}
