// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.Attribute;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.FieldVisitor;

public class FieldVisitorTee extends FieldVisitor
{
    private FieldVisitor fv1;
    private FieldVisitor fv2;
    
    public FieldVisitorTee(final FieldVisitor fv1, final FieldVisitor fv2) {
        super(262144);
        this.fv1 = fv1;
        this.fv2 = fv2;
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return AnnotationVisitorTee.getInstance(this.fv1.visitAnnotation(desc, visible), this.fv2.visitAnnotation(desc, visible));
    }
    
    public void visitAttribute(final Attribute attr) {
        this.fv1.visitAttribute(attr);
        this.fv2.visitAttribute(attr);
    }
    
    public void visitEnd() {
        this.fv1.visitEnd();
        this.fv2.visitEnd();
    }
}
