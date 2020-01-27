// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;

public interface SpelNode
{
    Object getValue(final ExpressionState p0) throws EvaluationException;
    
    TypedValue getTypedValue(final ExpressionState p0) throws EvaluationException;
    
    boolean isWritable(final ExpressionState p0) throws EvaluationException;
    
    void setValue(final ExpressionState p0, final Object p1) throws EvaluationException;
    
    String toStringAST();
    
    int getChildCount();
    
    SpelNode getChild(final int p0);
    
    Class<?> getObjectClass(final Object p0);
    
    int getStartPosition();
    
    int getEndPosition();
}
