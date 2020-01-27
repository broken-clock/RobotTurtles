// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class NullInstantiator<T> implements ObjectInstantiator<T>
{
    public NullInstantiator(final Class<T> type) {
    }
    
    public T newInstance() {
        return null;
    }
}
