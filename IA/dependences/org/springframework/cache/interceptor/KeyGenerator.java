// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

public interface KeyGenerator
{
    Object generate(final Object p0, final Method p1, final Object... p2);
}
