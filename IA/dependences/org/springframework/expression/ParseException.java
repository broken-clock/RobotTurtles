// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public class ParseException extends ExpressionException
{
    public ParseException(final String expressionString, final int position, final String message) {
        super(expressionString, position, message);
    }
    
    public ParseException(final int position, final String message, final Throwable cause) {
        super(position, message, cause);
    }
    
    public ParseException(final int position, final String message) {
        super(position, message);
    }
}
