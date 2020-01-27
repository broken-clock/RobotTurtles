// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.io.Serializable;

class TrueClassFilter implements ClassFilter, Serializable
{
    public static final TrueClassFilter INSTANCE;
    
    private TrueClassFilter() {
    }
    
    @Override
    public boolean matches(final Class<?> clazz) {
        return true;
    }
    
    private Object readResolve() {
        return TrueClassFilter.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "ClassFilter.TRUE";
    }
    
    static {
        INSTANCE = new TrueClassFilter();
    }
}
