// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;

public abstract class AbstractClassFilterTransformer extends AbstractClassTransformer
{
    private ClassTransformer pass;
    private ClassVisitor target;
    
    public void setTarget(final ClassVisitor target) {
        super.setTarget(target);
        this.pass.setTarget(target);
    }
    
    protected AbstractClassFilterTransformer(final ClassTransformer pass) {
        this.pass = pass;
    }
    
    protected abstract boolean accept(final int p0, final int p1, final String p2, final String p3, final String p4, final String[] p5);
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        (this.target = (this.accept(version, access, name, signature, superName, interfaces) ? this.pass : this.cv)).visit(version, access, name, signature, superName, interfaces);
    }
    
    public void visitSource(final String source, final String debug) {
        this.target.visitSource(source, debug);
    }
    
    public void visitOuterClass(final String owner, final String name, final String desc) {
        this.target.visitOuterClass(owner, name, desc);
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return this.target.visitAnnotation(desc, visible);
    }
    
    public void visitAttribute(final Attribute attr) {
        this.target.visitAttribute(attr);
    }
    
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        this.target.visitInnerClass(name, outerName, innerName, access);
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        return this.target.visitField(access, name, desc, signature, value);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return this.target.visitMethod(access, name, desc, signature, exceptions);
    }
    
    public void visitEnd() {
        this.target.visitEnd();
        this.target = null;
    }
}
