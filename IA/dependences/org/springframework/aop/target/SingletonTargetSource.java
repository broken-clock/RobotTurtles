// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.util.ObjectUtils;
import org.springframework.util.Assert;
import java.io.Serializable;
import org.springframework.aop.TargetSource;

public class SingletonTargetSource implements TargetSource, Serializable
{
    private static final long serialVersionUID = 9031246629662423738L;
    private final Object target;
    
    public SingletonTargetSource(final Object target) {
        Assert.notNull(target, "Target object must not be null");
        this.target = target;
    }
    
    @Override
    public Class<?> getTargetClass() {
        return this.target.getClass();
    }
    
    @Override
    public Object getTarget() {
        return this.target;
    }
    
    @Override
    public void releaseTarget(final Object target) {
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SingletonTargetSource)) {
            return false;
        }
        final SingletonTargetSource otherTargetSource = (SingletonTargetSource)other;
        return this.target.equals(otherTargetSource.target);
    }
    
    @Override
    public int hashCode() {
        return this.target.hashCode();
    }
    
    @Override
    public String toString() {
        return "SingletonTargetSource for target object [" + ObjectUtils.identityToString(this.target) + "]";
    }
}
