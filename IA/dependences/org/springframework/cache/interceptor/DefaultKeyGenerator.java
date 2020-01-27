// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

@Deprecated
public class DefaultKeyGenerator implements KeyGenerator
{
    public static final int NO_PARAM_KEY = 0;
    public static final int NULL_PARAM_KEY = 53;
    
    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        if (params.length == 1) {
            return (params[0] == null) ? Integer.valueOf(53) : params[0];
        }
        if (params.length == 0) {
            return 0;
        }
        int hashCode = 17;
        for (final Object object : params) {
            hashCode = 31 * hashCode + ((object == null) ? 53 : object.hashCode());
        }
        return hashCode;
    }
}
