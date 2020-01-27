// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class MethodInfoTransformer implements Transformer
{
    private static final MethodInfoTransformer INSTANCE;
    
    public static MethodInfoTransformer getInstance() {
        return MethodInfoTransformer.INSTANCE;
    }
    
    public Object transform(final Object value) {
        if (value instanceof Method) {
            return ReflectUtils.getMethodInfo((Member)value);
        }
        if (value instanceof Constructor) {
            return ReflectUtils.getMethodInfo((Member)value);
        }
        throw new IllegalArgumentException("cannot get method info for " + value);
    }
    
    static {
        INSTANCE = new MethodInfoTransformer();
    }
}
