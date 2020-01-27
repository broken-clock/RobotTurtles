// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassGenerator;

public class TransformingClassGenerator implements ClassGenerator
{
    private ClassGenerator gen;
    private ClassTransformer t;
    
    public TransformingClassGenerator(final ClassGenerator gen, final ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }
    
    public void generateClass(final ClassVisitor v) throws Exception {
        this.t.setTarget(v);
        this.gen.generateClass(this.t);
    }
}
