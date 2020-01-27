// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice
{
    void before(final Method p0, final Object[] p1, final Object p2) throws Throwable;
}
