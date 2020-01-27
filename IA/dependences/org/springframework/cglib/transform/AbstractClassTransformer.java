// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;

public abstract class AbstractClassTransformer extends ClassTransformer
{
    protected AbstractClassTransformer() {
        super(262144);
    }
    
    public void setTarget(final ClassVisitor target) {
        this.cv = target;
    }
}
