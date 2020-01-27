// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.StdInstantiatorStrategy;

public class ObjenesisStd extends ObjenesisBase
{
    public ObjenesisStd() {
        super(new StdInstantiatorStrategy());
    }
    
    public ObjenesisStd(final boolean useCache) {
        super(new StdInstantiatorStrategy(), useCache);
    }
}
