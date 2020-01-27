// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.ObjectUtils;
import java.lang.reflect.Method;
import org.springframework.util.Assert;

public class LookupOverride extends MethodOverride
{
    private final String beanName;
    
    public LookupOverride(final String methodName, final String beanName) {
        super(methodName);
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanName = beanName;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    @Override
    public boolean matches(final Method method) {
        return method.getName().equals(this.getMethodName()) && method.getParameterTypes().length == 0;
    }
    
    @Override
    public String toString() {
        return "LookupOverride for method '" + this.getMethodName() + "'; will return bean '" + this.beanName + "'";
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof LookupOverride && super.equals(other) && ObjectUtils.nullSafeEquals(this.beanName, ((LookupOverride)other).beanName);
    }
    
    @Override
    public int hashCode() {
        return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName);
    }
}
