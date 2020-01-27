// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.support;

import org.springframework.cache.Cache;

public class SimpleValueWrapper implements Cache.ValueWrapper
{
    private final Object value;
    
    public SimpleValueWrapper(final Object value) {
        this.value = value;
    }
    
    @Override
    public Object get() {
        return this.value;
    }
}
