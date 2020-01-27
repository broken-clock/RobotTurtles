// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.common;

import org.springframework.expression.ParserContext;

public class TemplateParserContext implements ParserContext
{
    private final String expressionPrefix;
    private final String expressionSuffix;
    
    public TemplateParserContext() {
        this("#{", "}");
    }
    
    public TemplateParserContext(final String expressionPrefix, final String expressionSuffix) {
        this.expressionPrefix = expressionPrefix;
        this.expressionSuffix = expressionSuffix;
    }
    
    @Override
    public final boolean isTemplate() {
        return true;
    }
    
    @Override
    public final String getExpressionPrefix() {
        return this.expressionPrefix;
    }
    
    @Override
    public final String getExpressionSuffix() {
        return this.expressionSuffix;
    }
}
