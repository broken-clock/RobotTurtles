// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Callback
{
    Object intercept(final Object p0, final Method p1, final Object[] p2, final MethodProxy p3) throws Throwable;
}
