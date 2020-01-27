// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MethodOverrides
{
    private final Set<MethodOverride> overrides;
    
    public MethodOverrides() {
        this.overrides = new HashSet<MethodOverride>(0);
    }
    
    public MethodOverrides(final MethodOverrides other) {
        this.overrides = new HashSet<MethodOverride>(0);
        this.addOverrides(other);
    }
    
    public void addOverrides(final MethodOverrides other) {
        if (other != null) {
            this.overrides.addAll(other.getOverrides());
        }
    }
    
    public void addOverride(final MethodOverride override) {
        this.overrides.add(override);
    }
    
    public Set<MethodOverride> getOverrides() {
        return this.overrides;
    }
    
    public boolean isEmpty() {
        return this.overrides.isEmpty();
    }
    
    public MethodOverride getOverride(final Method method) {
        for (final MethodOverride override : this.overrides) {
            if (override.matches(method)) {
                return override;
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodOverrides)) {
            return false;
        }
        final MethodOverrides that = (MethodOverrides)other;
        return this.overrides.equals(that.overrides);
    }
    
    @Override
    public int hashCode() {
        return this.overrides.hashCode();
    }
}
