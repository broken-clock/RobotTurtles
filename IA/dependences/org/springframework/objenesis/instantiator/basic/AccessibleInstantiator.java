// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator.basic;

public class AccessibleInstantiator<T> extends ConstructorInstantiator<T>
{
    public AccessibleInstantiator(final Class<T> type) {
        super(type);
        if (this.constructor != null) {
            this.constructor.setAccessible(true);
        }
    }
}
