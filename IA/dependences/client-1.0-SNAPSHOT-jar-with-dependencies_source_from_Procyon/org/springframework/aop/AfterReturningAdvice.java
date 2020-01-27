// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends AfterAdvice
{
    void afterReturning(final Object p0, final Method p1, final Object[] p2, final Object p3) throws Throwable;
}
