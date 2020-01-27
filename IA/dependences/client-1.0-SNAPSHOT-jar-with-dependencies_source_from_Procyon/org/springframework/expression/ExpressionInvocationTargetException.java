// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public class ExpressionInvocationTargetException extends EvaluationException
{
    public ExpressionInvocationTargetException(final int position, final String message, final Throwable cause) {
        super(position, message, cause);
    }
    
    public ExpressionInvocationTargetException(final int position, final String message) {
        super(position, message);
    }
    
    public ExpressionInvocationTargetException(final String expressionString, final String message) {
        super(expressionString, message);
    }
    
    public ExpressionInvocationTargetException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ExpressionInvocationTargetException(final String message) {
        super(message);
    }
}
