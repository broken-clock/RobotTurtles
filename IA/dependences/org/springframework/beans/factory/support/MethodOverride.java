// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.ObjectUtils;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import org.springframework.beans.BeanMetadataElement;

public abstract class MethodOverride implements BeanMetadataElement
{
    private final String methodName;
    private boolean overloaded;
    private Object source;
    
    protected MethodOverride(final String methodName) {
        this.overloaded = true;
        Assert.notNull(methodName, "Method name must not be null");
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    protected void setOverloaded(final boolean overloaded) {
        this.overloaded = overloaded;
    }
    
    protected boolean isOverloaded() {
        return this.overloaded;
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public abstract boolean matches(final Method p0);
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodOverride)) {
            return false;
        }
        final MethodOverride that = (MethodOverride)other;
        return ObjectUtils.nullSafeEquals(this.methodName, that.methodName) && ObjectUtils.nullSafeEquals(this.source, that.source);
    }
    
    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.methodName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.source);
        return hashCode;
    }
}
