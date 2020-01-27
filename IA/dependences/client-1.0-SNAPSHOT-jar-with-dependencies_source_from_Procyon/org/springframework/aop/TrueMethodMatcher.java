// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.lang.reflect.Method;
import java.io.Serializable;

class TrueMethodMatcher implements MethodMatcher, Serializable
{
    public static final TrueMethodMatcher INSTANCE;
    
    private TrueMethodMatcher() {
    }
    
    @Override
    public boolean isRuntime() {
        return false;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        return true;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass, final Object[] args) {
        throw new UnsupportedOperationException();
    }
    
    private Object readResolve() {
        return TrueMethodMatcher.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "MethodMatcher.TRUE";
    }
    
    static {
        INSTANCE = new TrueMethodMatcher();
    }
}
