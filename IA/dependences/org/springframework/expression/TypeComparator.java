// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface TypeComparator
{
    boolean canCompare(final Object p0, final Object p1);
    
    int compare(final Object p0, final Object p1) throws EvaluationException;
}
