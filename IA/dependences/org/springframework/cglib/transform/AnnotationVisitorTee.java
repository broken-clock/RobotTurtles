// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;

public class AnnotationVisitorTee extends AnnotationVisitor
{
    private AnnotationVisitor av1;
    private AnnotationVisitor av2;
    
    public static AnnotationVisitor getInstance(final AnnotationVisitor av1, final AnnotationVisitor av2) {
        if (av1 == null) {
            return av2;
        }
        if (av2 == null) {
            return av1;
        }
        return new AnnotationVisitorTee(av1, av2);
    }
    
    public AnnotationVisitorTee(final AnnotationVisitor av1, final AnnotationVisitor av2) {
        super(262144);
        this.av1 = av1;
        this.av2 = av2;
    }
    
    public void visit(final String name, final Object value) {
        this.av2.visit(name, value);
        this.av2.visit(name, value);
    }
    
    public void visitEnum(final String name, final String desc, final String value) {
        this.av1.visitEnum(name, desc, value);
        this.av2.visitEnum(name, desc, value);
    }
    
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        return getInstance(this.av1.visitAnnotation(name, desc), this.av2.visitAnnotation(name, desc));
    }
    
    public AnnotationVisitor visitArray(final String name) {
        return getInstance(this.av1.visitArray(name), this.av2.visitArray(name));
    }
    
    public void visitEnd() {
        this.av1.visitEnd();
        this.av2.visitEnd();
    }
}
