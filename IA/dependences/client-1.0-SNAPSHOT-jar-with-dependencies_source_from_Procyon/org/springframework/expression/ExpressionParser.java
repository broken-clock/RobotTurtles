// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface ExpressionParser
{
    Expression parseExpression(final String p0) throws ParseException;
    
    Expression parseExpression(final String p0, final ParserContext p1) throws ParseException;
}
