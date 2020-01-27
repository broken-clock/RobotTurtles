// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public class ExpressionException extends RuntimeException
{
    protected String expressionString;
    protected int position;
    
    public ExpressionException(final String expressionString, final String message) {
        super(message);
        this.position = -1;
        this.expressionString = expressionString;
    }
    
    public ExpressionException(final String expressionString, final int position, final String message) {
        super(message);
        this.position = position;
        this.expressionString = expressionString;
    }
    
    public ExpressionException(final int position, final String message) {
        super(message);
        this.position = position;
    }
    
    public ExpressionException(final int position, final String message, final Throwable cause) {
        super(message, cause);
        this.position = position;
    }
    
    public ExpressionException(final String message) {
        super(message);
    }
    
    public ExpressionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getMessage() {
        return this.toDetailedString();
    }
    
    public String getSimpleMessage() {
        return super.getMessage();
    }
    
    public String toDetailedString() {
        final StringBuilder output = new StringBuilder();
        if (this.expressionString != null) {
            output.append("Expression '");
            output.append(this.expressionString);
            output.append("'");
            if (this.position != -1) {
                output.append(" @ ");
                output.append(this.position);
            }
            output.append(": ");
        }
        output.append(this.getSimpleMessage());
        return output.toString();
    }
    
    public final String getExpressionString() {
        return this.expressionString;
    }
    
    public final int getPosition() {
        return this.position;
    }
}
