// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;

public abstract class ClassTransformer extends ClassVisitor
{
    public ClassTransformer() {
        super(262144);
    }
    
    public ClassTransformer(final int opcode) {
        super(opcode);
    }
    
    public abstract void setTarget(final ClassVisitor p0);
}
