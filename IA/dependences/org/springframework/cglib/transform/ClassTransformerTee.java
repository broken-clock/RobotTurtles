// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;

public class ClassTransformerTee extends ClassTransformer
{
    private ClassVisitor branch;
    
    public ClassTransformerTee(final ClassVisitor branch) {
        super(262144);
        this.branch = branch;
    }
    
    public void setTarget(final ClassVisitor target) {
        this.cv = new ClassVisitorTee(this.branch, target);
    }
}
