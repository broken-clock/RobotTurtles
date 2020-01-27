// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform.impl;

import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;
import org.springframework.cglib.transform.MethodFilterTransformer;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.transform.MethodFilter;
import org.springframework.cglib.core.DefaultGeneratorStrategy;

public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy
{
    private Class wrapper;
    private static final MethodFilter TRANSFORM_FILTER;
    
    public UndeclaredThrowableStrategy(final Class wrapper) {
        this.wrapper = wrapper;
    }
    
    protected ClassGenerator transform(final ClassGenerator cg) throws Exception {
        ClassTransformer tr = new UndeclaredThrowableTransformer(this.wrapper);
        tr = new MethodFilterTransformer(UndeclaredThrowableStrategy.TRANSFORM_FILTER, tr);
        return new TransformingClassGenerator(cg, tr);
    }
    
    static {
        TRANSFORM_FILTER = new MethodFilter() {
            public boolean accept(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                return !TypeUtils.isPrivate(access) && name.indexOf(36) < 0;
            }
        };
    }
}
