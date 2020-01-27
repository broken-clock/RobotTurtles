// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

public interface ClassFilter
{
    public static final ClassFilter TRUE = TrueClassFilter.INSTANCE;
    
    boolean matches(final Class<?> p0);
}
