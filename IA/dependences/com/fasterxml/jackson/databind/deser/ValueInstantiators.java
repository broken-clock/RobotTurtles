// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;

public interface ValueInstantiators
{
    ValueInstantiator findValueInstantiator(final DeserializationConfig p0, final BeanDescription p1, final ValueInstantiator p2);
    
    public static class Base implements ValueInstantiators
    {
        @Override
        public ValueInstantiator findValueInstantiator(final DeserializationConfig config, final BeanDescription beanDesc, final ValueInstantiator defaultInstantiator) {
            return defaultInstantiator;
        }
    }
}
