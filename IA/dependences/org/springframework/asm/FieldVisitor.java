// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.asm;

public abstract class FieldVisitor
{
    protected final int api;
    protected FieldVisitor fv;
    
    public FieldVisitor(final int api) {
        this(api, null);
    }
    
    public FieldVisitor(final int api, final FieldVisitor fv) {
        if (api != 262144) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.fv = fv;
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(desc, visible);
        }
        return null;
    }
    
    public void visitAttribute(final Attribute attr) {
        if (this.fv != null) {
            this.fv.visitAttribute(attr);
        }
    }
    
    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}
