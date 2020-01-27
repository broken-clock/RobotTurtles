// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;

public class RootClassFilter implements ClassFilter, Serializable
{
    private Class<?> clazz;
    
    public RootClassFilter(final Class<?> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public boolean matches(final Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }
}
