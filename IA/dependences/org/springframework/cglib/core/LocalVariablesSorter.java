// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.asm.MethodVisitor;

public class LocalVariablesSorter extends MethodVisitor
{
    protected final int firstLocal;
    private final State state;
    
    public LocalVariablesSorter(final int access, final String desc, final MethodVisitor mv) {
        super(262144, mv);
        this.state = new State();
        final Type[] args = Type.getArgumentTypes(desc);
        this.state.nextLocal = (((0x8 & access) == 0x0) ? 1 : 0);
        for (int i = 0; i < args.length; ++i) {
            final State state = this.state;
            state.nextLocal += args[i].getSize();
        }
        this.firstLocal = this.state.nextLocal;
    }
    
    public LocalVariablesSorter(final LocalVariablesSorter lvs) {
        super(262144, lvs.mv);
        this.state = lvs.state;
        this.firstLocal = lvs.firstLocal;
    }
    
    public void visitVarInsn(final int opcode, final int var) {
        int size = 0;
        switch (opcode) {
            case 22:
            case 24:
            case 55:
            case 57: {
                size = 2;
                break;
            }
            default: {
                size = 1;
                break;
            }
        }
        this.mv.visitVarInsn(opcode, this.remap(var, size));
    }
    
    public void visitIincInsn(final int var, final int increment) {
        this.mv.visitIincInsn(this.remap(var, 1), increment);
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitMaxs(maxStack, this.state.nextLocal);
    }
    
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        this.mv.visitLocalVariable(name, desc, signature, start, end, this.remap(index));
    }
    
    protected int newLocal(final int size) {
        final int var = this.state.nextLocal;
        final State state = this.state;
        state.nextLocal += size;
        return var;
    }
    
    private int remap(final int var, final int size) {
        if (var < this.firstLocal) {
            return var;
        }
        final int key = 2 * var + size - 1;
        final int length = this.state.mapping.length;
        if (key >= length) {
            final int[] newMapping = new int[Math.max(2 * length, key + 1)];
            System.arraycopy(this.state.mapping, 0, newMapping, 0, length);
            this.state.mapping = newMapping;
        }
        int value = this.state.mapping[key];
        if (value == 0) {
            value = this.state.nextLocal + 1;
            this.state.mapping[key] = value;
            final State state = this.state;
            state.nextLocal += size;
        }
        return value - 1;
    }
    
    private int remap(final int var) {
        if (var < this.firstLocal) {
            return var;
        }
        final int key = 2 * var;
        int value = (key < this.state.mapping.length) ? this.state.mapping[key] : 0;
        if (value == 0) {
            value = ((key + 1 < this.state.mapping.length) ? this.state.mapping[key + 1] : 0);
        }
        if (value == 0) {
            throw new IllegalStateException("Unknown local variable " + var);
        }
        return value - 1;
    }
    
    private static class State
    {
        int[] mapping;
        int nextLocal;
        
        private State() {
            this.mapping = new int[40];
        }
    }
}
