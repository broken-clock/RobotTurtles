// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.ObjectUtils;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import java.util.LinkedList;
import java.util.List;

public class ReplaceOverride extends MethodOverride
{
    private final String methodReplacerBeanName;
    private List<String> typeIdentifiers;
    
    public ReplaceOverride(final String methodName, final String methodReplacerBeanName) {
        super(methodName);
        this.typeIdentifiers = new LinkedList<String>();
        Assert.notNull(methodName, "Method replacer bean name must not be null");
        this.methodReplacerBeanName = methodReplacerBeanName;
    }
    
    public String getMethodReplacerBeanName() {
        return this.methodReplacerBeanName;
    }
    
    public void addTypeIdentifier(final String identifier) {
        this.typeIdentifiers.add(identifier);
    }
    
    @Override
    public boolean matches(final Method method) {
        if (!method.getName().equals(this.getMethodName())) {
            return false;
        }
        if (!this.isOverloaded()) {
            return true;
        }
        if (this.typeIdentifiers.size() != method.getParameterTypes().length) {
            return false;
        }
        for (int i = 0; i < this.typeIdentifiers.size(); ++i) {
            final String identifier = this.typeIdentifiers.get(i);
            if (!method.getParameterTypes()[i].getName().contains(identifier)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Replace override for method '" + this.getMethodName() + "; will call bean '" + this.methodReplacerBeanName + "'";
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ReplaceOverride) || !super.equals(other)) {
            return false;
        }
        final ReplaceOverride that = (ReplaceOverride)other;
        return ObjectUtils.nullSafeEquals(this.methodReplacerBeanName, that.methodReplacerBeanName) && ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers);
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.methodReplacerBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
        return hashCode;
    }
}
