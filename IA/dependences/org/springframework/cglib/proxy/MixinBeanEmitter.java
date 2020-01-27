// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.ReflectUtils;
import java.lang.reflect.Method;
import org.springframework.asm.ClassVisitor;

class MixinBeanEmitter extends MixinEmitter
{
    public MixinBeanEmitter(final ClassVisitor v, final String className, final Class[] classes) {
        super(v, className, classes, null);
    }
    
    protected Class[] getInterfaces(final Class[] classes) {
        return null;
    }
    
    protected Method[] getMethods(final Class type) {
        return ReflectUtils.getPropertyMethods(ReflectUtils.getBeanProperties(type), true, true);
    }
}
