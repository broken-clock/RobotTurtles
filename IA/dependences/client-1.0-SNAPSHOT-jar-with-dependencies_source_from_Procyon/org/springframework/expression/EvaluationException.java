// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public class EvaluationException extends ExpressionException
{
    public EvaluationException(final int position, final String message) {
        super(position, message);
    }
    
    public EvaluationException(final String expressionString, final String message) {
        super(expressionString, message);
    }
    
    public EvaluationException(final int position, final String message, final Throwable cause) {
        super(position, message, cause);
    }
    
    public EvaluationException(final String message) {
        super(message);
    }
    
    public EvaluationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
