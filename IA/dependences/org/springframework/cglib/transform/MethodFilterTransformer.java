// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.ClassVisitor;

public class MethodFilterTransformer extends AbstractClassTransformer
{
    private MethodFilter filter;
    private ClassTransformer pass;
    private ClassVisitor direct;
    
    public MethodFilterTransformer(final MethodFilter filter, final ClassTransformer pass) {
        this.filter = filter;
        super.setTarget(this.pass = pass);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return (this.filter.accept(access, name, desc, signature, exceptions) ? this.pass : this.direct).visitMethod(access, name, desc, signature, exceptions);
    }
    
    public void setTarget(final ClassVisitor target) {
        this.pass.setTarget(target);
        this.direct = target;
    }
}
