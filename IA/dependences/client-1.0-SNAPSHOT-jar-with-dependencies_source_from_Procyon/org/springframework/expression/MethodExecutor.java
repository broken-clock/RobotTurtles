// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface MethodExecutor
{
    TypedValue execute(final EvaluationContext p0, final Object p1, final Object... p2) throws AccessException;
}
