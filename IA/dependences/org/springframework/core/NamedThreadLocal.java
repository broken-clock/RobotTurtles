// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import org.springframework.util.Assert;

public class NamedThreadLocal<T> extends ThreadLocal<T>
{
    private final String name;
    
    public NamedThreadLocal(final String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
