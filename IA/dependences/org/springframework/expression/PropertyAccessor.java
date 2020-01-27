// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface PropertyAccessor
{
    Class<?>[] getSpecificTargetClasses();
    
    boolean canRead(final EvaluationContext p0, final Object p1, final String p2) throws AccessException;
    
    TypedValue read(final EvaluationContext p0, final Object p1, final String p2) throws AccessException;
    
    boolean canWrite(final EvaluationContext p0, final Object p1, final String p2) throws AccessException;
    
    void write(final EvaluationContext p0, final Object p1, final String p2, final Object p3) throws AccessException;
}
