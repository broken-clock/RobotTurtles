// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class FailingInstantiator<T> implements ObjectInstantiator<T>
{
    public FailingInstantiator(final Class<T> type) {
    }
    
    public T newInstance() {
        throw new ObjenesisException("Always failing");
    }
}
