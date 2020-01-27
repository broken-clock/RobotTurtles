// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

public class SimpleKeyGenerator implements KeyGenerator
{
    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1 && params[0] != null) {
            return params[0];
        }
        return new SimpleKey(params);
    }
}
