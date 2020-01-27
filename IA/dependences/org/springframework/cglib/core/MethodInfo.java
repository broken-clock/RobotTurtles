// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import org.springframework.asm.Type;

public abstract class MethodInfo
{
    protected MethodInfo() {
    }
    
    public abstract ClassInfo getClassInfo();
    
    public abstract int getModifiers();
    
    public abstract Signature getSignature();
    
    public abstract Type[] getExceptionTypes();
    
    public boolean equals(final Object o) {
        return o != null && o instanceof MethodInfo && this.getSignature().equals(((MethodInfo)o).getSignature());
    }
    
    public int hashCode() {
        return this.getSignature().hashCode();
    }
    
    public String toString() {
        return this.getSignature().toString();
    }
}
