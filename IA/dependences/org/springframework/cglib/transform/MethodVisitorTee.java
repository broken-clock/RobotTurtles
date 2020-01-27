// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.Label;
import org.springframework.asm.Attribute;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;

public class MethodVisitorTee extends MethodVisitor
{
    private final MethodVisitor mv1;
    private final MethodVisitor mv2;
    
    public MethodVisitorTee(final MethodVisitor mv1, final MethodVisitor mv2) {
        super(262144);
        this.mv1 = mv1;
        this.mv2 = mv2;
    }
    
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        this.mv1.visitFrame(type, nLocal, local, nStack, stack);
        this.mv2.visitFrame(type, nLocal, local, nStack, stack);
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
        return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotationDefault(), this.mv2.visitAnnotationDefault());
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotation(desc, visible), this.mv2.visitAnnotation(desc, visible));
    }
    
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitParameterAnnotation(parameter, desc, visible), this.mv2.visitParameterAnnotation(parameter, desc, visible));
    }
    
    public void visitAttribute(final Attribute attr) {
        this.mv1.visitAttribute(attr);
        this.mv2.visitAttribute(attr);
    }
    
    public void visitCode() {
        this.mv1.visitCode();
        this.mv2.visitCode();
    }
    
    public void visitInsn(final int opcode) {
        this.mv1.visitInsn(opcode);
        this.mv2.visitInsn(opcode);
    }
    
    public void visitIntInsn(final int opcode, final int operand) {
        this.mv1.visitIntInsn(opcode, operand);
        this.mv2.visitIntInsn(opcode, operand);
    }
    
    public void visitVarInsn(final int opcode, final int var) {
        this.mv1.visitVarInsn(opcode, var);
        this.mv2.visitVarInsn(opcode, var);
    }
    
    public void visitTypeInsn(final int opcode, final String desc) {
        this.mv1.visitTypeInsn(opcode, desc);
        this.mv2.visitTypeInsn(opcode, desc);
    }
    
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        this.mv1.visitFieldInsn(opcode, owner, name, desc);
        this.mv2.visitFieldInsn(opcode, owner, name, desc);
    }
    
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        this.mv1.visitMethodInsn(opcode, owner, name, desc);
        this.mv2.visitMethodInsn(opcode, owner, name, desc);
    }
    
    public void visitJumpInsn(final int opcode, final Label label) {
        this.mv1.visitJumpInsn(opcode, label);
        this.mv2.visitJumpInsn(opcode, label);
    }
    
    public void visitLabel(final Label label) {
        this.mv1.visitLabel(label);
        this.mv2.visitLabel(label);
    }
    
    public void visitLdcInsn(final Object cst) {
        this.mv1.visitLdcInsn(cst);
        this.mv2.visitLdcInsn(cst);
    }
    
    public void visitIincInsn(final int var, final int increment) {
        this.mv1.visitIincInsn(var, increment);
        this.mv2.visitIincInsn(var, increment);
    }
    
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
        this.mv1.visitTableSwitchInsn(min, max, dflt, labels);
        this.mv2.visitTableSwitchInsn(min, max, dflt, labels);
    }
    
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.mv1.visitLookupSwitchInsn(dflt, keys, labels);
        this.mv2.visitLookupSwitchInsn(dflt, keys, labels);
    }
    
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        this.mv1.visitMultiANewArrayInsn(desc, dims);
        this.mv2.visitMultiANewArrayInsn(desc, dims);
    }
    
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        this.mv1.visitTryCatchBlock(start, end, handler, type);
        this.mv2.visitTryCatchBlock(start, end, handler, type);
    }
    
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        this.mv1.visitLocalVariable(name, desc, signature, start, end, index);
        this.mv2.visitLocalVariable(name, desc, signature, start, end, index);
    }
    
    public void visitLineNumber(final int line, final Label start) {
        this.mv1.visitLineNumber(line, start);
        this.mv2.visitLineNumber(line, start);
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv1.visitMaxs(maxStack, maxLocals);
        this.mv2.visitMaxs(maxStack, maxLocals);
    }
    
    public void visitEnd() {
        this.mv1.visitEnd();
        this.mv2.visitEnd();
    }
}
