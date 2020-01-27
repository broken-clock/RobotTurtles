// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface OperatorOverloader
{
    boolean overridesOperation(final Operation p0, final Object p1, final Object p2) throws EvaluationException;
    
    Object operate(final Operation p0, final Object p1, final Object p2) throws EvaluationException;
}
