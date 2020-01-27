// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.gcj;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;

public class GCJSerializationInstantiator<T> extends GCJInstantiatorBase<T>
{
    private Class<? super T> superType;
    
    public GCJSerializationInstantiator(final Class<T> type) {
        super(type);
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
    }
    
    @Override
    public T newInstance() {
        try {
            return this.type.cast(GCJSerializationInstantiator.newObjectMethod.invoke(GCJSerializationInstantiator.dummyStream, this.type, this.superType));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
