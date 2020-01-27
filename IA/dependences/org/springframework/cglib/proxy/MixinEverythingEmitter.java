// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.RejectModifierPredicate;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.cglib.core.ReflectUtils;
import java.util.ArrayList;
import org.springframework.asm.ClassVisitor;

class MixinEverythingEmitter extends MixinEmitter
{
    public MixinEverythingEmitter(final ClassVisitor v, final String className, final Class[] classes) {
        super(v, className, classes, null);
    }
    
    protected Class[] getInterfaces(final Class[] classes) {
        final List list = new ArrayList();
        for (int i = 0; i < classes.length; ++i) {
            ReflectUtils.addAllInterfaces(classes[i], list);
        }
        return list.toArray(new Class[list.size()]);
    }
    
    protected Method[] getMethods(final Class type) {
        final List methods = new ArrayList(Arrays.asList(type.getMethods()));
        CollectionUtils.filter(methods, new RejectModifierPredicate(24));
        return methods.toArray(new Method[methods.size()]);
    }
}
