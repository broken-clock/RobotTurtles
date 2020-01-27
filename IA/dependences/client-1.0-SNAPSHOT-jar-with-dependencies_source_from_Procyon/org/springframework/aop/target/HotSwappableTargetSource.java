// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.util.Assert;
import java.io.Serializable;
import org.springframework.aop.TargetSource;

public class HotSwappableTargetSource implements TargetSource, Serializable
{
    private static final long serialVersionUID = 7497929212653839187L;
    private Object target;
    
    public HotSwappableTargetSource(final Object initialTarget) {
        Assert.notNull(initialTarget, "Target object must not be null");
        this.target = initialTarget;
    }
    
    @Override
    public synchronized Class<?> getTargetClass() {
        return this.target.getClass();
    }
    
    @Override
    public final boolean isStatic() {
        return false;
    }
    
    @Override
    public synchronized Object getTarget() {
        return this.target;
    }
    
    @Override
    public void releaseTarget(final Object target) {
    }
    
    public synchronized Object swap(final Object newTarget) throws IllegalArgumentException {
        Assert.notNull(newTarget, "Target object must not be null");
        final Object old = this.target;
        this.target = newTarget;
        return old;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof HotSwappableTargetSource && this.target.equals(((HotSwappableTargetSource)other).target));
    }
    
    @Override
    public int hashCode() {
        return HotSwappableTargetSource.class.hashCode();
    }
    
    @Override
    public String toString() {
        return "HotSwappableTargetSource for target: " + this.target;
    }
}
