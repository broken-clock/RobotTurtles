// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.SerializingInstantiatorStrategy;

public class ObjenesisSerializer extends ObjenesisBase
{
    public ObjenesisSerializer() {
        super(new SerializingInstantiatorStrategy());
    }
    
    public ObjenesisSerializer(final boolean useCache) {
        super(new SerializingInstantiatorStrategy(), useCache);
    }
}
