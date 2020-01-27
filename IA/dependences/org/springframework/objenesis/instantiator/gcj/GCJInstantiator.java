// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.gcj;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;

public class GCJInstantiator<T> extends GCJInstantiatorBase<T>
{
    public GCJInstantiator(final Class<T> type) {
        super(type);
    }
    
    @Override
    public T newInstance() {
        try {
            return this.type.cast(GCJInstantiator.newObjectMethod.invoke(GCJInstantiator.dummyStream, this.type, Object.class));
        }
        catch (RuntimeException e) {
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
