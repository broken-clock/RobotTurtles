// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.standard;

import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.util.Assert;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.common.TemplateAwareExpressionParser;

public class SpelExpressionParser extends TemplateAwareExpressionParser
{
    private final SpelParserConfiguration configuration;
    
    public SpelExpressionParser() {
        this.configuration = new SpelParserConfiguration(false, false);
    }
    
    public SpelExpressionParser(final SpelParserConfiguration configuration) {
        Assert.notNull(configuration, "SpelParserConfiguration must not be null");
        this.configuration = configuration;
    }
    
    @Override
    protected SpelExpression doParseExpression(final String expressionString, final ParserContext context) throws ParseException {
        return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
    }
    
    public SpelExpression parseRaw(final String expressionString) throws ParseException {
        return this.doParseExpression(expressionString, null);
    }
}
