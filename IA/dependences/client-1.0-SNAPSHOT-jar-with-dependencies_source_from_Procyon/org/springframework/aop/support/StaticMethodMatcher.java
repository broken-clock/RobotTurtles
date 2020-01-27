// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;

public abstract class StaticMethodMatcher implements MethodMatcher
{
    @Override
    public final boolean isRuntime() {
        return false;
    }
    
    @Override
    public final boolean matches(final Method method, final Class<?> targetClass, final Object[] args) {
        throw new UnsupportedOperationException("Illegal MethodMatcher usage");
    }
}
