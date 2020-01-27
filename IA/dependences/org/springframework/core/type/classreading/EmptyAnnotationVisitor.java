// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.asm.AnnotationVisitor;

class EmptyAnnotationVisitor extends AnnotationVisitor
{
    public EmptyAnnotationVisitor() {
        super(262144);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        return this;
    }
    
    @Override
    public AnnotationVisitor visitArray(final String name) {
        return this;
    }
}
