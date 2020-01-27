// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

public enum Autowire
{
    NO(0), 
    BY_NAME(1), 
    BY_TYPE(2);
    
    private final int value;
    
    private Autowire(final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
    
    public boolean isAutowire() {
        return this == Autowire.BY_NAME || this == Autowire.BY_TYPE;
    }
}
