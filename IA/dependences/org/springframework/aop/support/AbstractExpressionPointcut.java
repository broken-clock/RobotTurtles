// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.Serializable;

public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable
{
    private String location;
    private String expression;
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setExpression(final String expression) {
        this.expression = expression;
        try {
            this.onSetExpression(expression);
        }
        catch (IllegalArgumentException ex) {
            if (this.location != null) {
                throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
            }
            throw ex;
        }
    }
    
    protected void onSetExpression(final String expression) throws IllegalArgumentException {
    }
    
    @Override
    public String getExpression() {
        return this.expression;
    }
}
