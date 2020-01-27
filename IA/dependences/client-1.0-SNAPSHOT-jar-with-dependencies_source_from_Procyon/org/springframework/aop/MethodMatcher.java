// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.lang.reflect.Method;

public interface MethodMatcher
{
    public static final MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;
    
    boolean matches(final Method p0, final Class<?> p1);
    
    boolean isRuntime();
    
    boolean matches(final Method p0, final Class<?> p1, final Object[] p2);
}
