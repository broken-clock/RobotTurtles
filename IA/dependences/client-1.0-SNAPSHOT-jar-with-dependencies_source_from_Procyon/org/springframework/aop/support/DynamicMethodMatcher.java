// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;

public abstract class DynamicMethodMatcher implements MethodMatcher
{
    @Override
    public final boolean isRuntime() {
        return true;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        return true;
    }
}
