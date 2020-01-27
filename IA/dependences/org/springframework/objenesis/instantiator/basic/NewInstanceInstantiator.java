// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class NewInstanceInstantiator<T> implements ObjectInstantiator<T>
{
    private final Class<T> type;
    
    public NewInstanceInstantiator(final Class<T> type) {
        this.type = type;
    }
    
    public T newInstance() {
        try {
            return this.type.newInstance();
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
