// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.cglib.core.ClassGenerator;
import org.springframework.asm.ClassReader;

public class TransformingClassLoader extends AbstractClassLoader
{
    private ClassTransformerFactory t;
    
    public TransformingClassLoader(final ClassLoader parent, final ClassFilter filter, final ClassTransformerFactory t) {
        super(parent, parent, filter);
        this.t = t;
    }
    
    protected ClassGenerator getGenerator(final ClassReader r) {
        final ClassTransformer t2 = this.t.newInstance();
        return new TransformingClassGenerator(super.getGenerator(r), t2);
    }
}
