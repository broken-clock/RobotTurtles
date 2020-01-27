// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import org.springframework.asm.Type;

public class Local
{
    private Type type;
    private int index;
    
    public Local(final int index, final Type type) {
        this.type = type;
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public Type getType() {
        return this.type;
    }
}
