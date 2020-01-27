// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.expression;

import org.springframework.beans.BeansException;
import org.springframework.core.convert.ConversionService;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.expression.BeanResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.PropertyAccessor;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.expression.Expression;
import java.util.Map;
import org.springframework.expression.ExpressionParser;
import org.springframework.beans.factory.config.BeanExpressionResolver;

public class StandardBeanExpressionResolver implements BeanExpressionResolver
{
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";
    private String expressionPrefix;
    private String expressionSuffix;
    private ExpressionParser expressionParser;
    private final Map<String, Expression> expressionCache;
    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache;
    private final ParserContext beanExpressionParserContext;
    
    public StandardBeanExpressionResolver() {
        this.expressionPrefix = "#{";
        this.expressionSuffix = "}";
        this.expressionParser = new SpelExpressionParser();
        this.expressionCache = new ConcurrentHashMap<String, Expression>(256);
        this.evaluationCache = new ConcurrentHashMap<BeanExpressionContext, StandardEvaluationContext>(8);
        this.beanExpressionParserContext = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return true;
            }
            
            @Override
            public String getExpressionPrefix() {
                return StandardBeanExpressionResolver.this.expressionPrefix;
            }
            
            @Override
            public String getExpressionSuffix() {
                return StandardBeanExpressionResolver.this.expressionSuffix;
            }
        };
    }
    
    public void setExpressionPrefix(final String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }
    
    public void setExpressionSuffix(final String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }
    
    public void setExpressionParser(final ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }
    
    @Override
    public Object evaluate(final String value, final BeanExpressionContext evalContext) throws BeansException {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
        try {
            Expression expr = this.expressionCache.get(value);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                this.expressionCache.put(value, expr);
            }
            StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
            if (sec == null) {
                sec = new StandardEvaluationContext();
                sec.setRootObject(evalContext);
                sec.addPropertyAccessor(new BeanExpressionContextAccessor());
                sec.addPropertyAccessor(new BeanFactoryAccessor());
                sec.addPropertyAccessor(new MapAccessor());
                sec.addPropertyAccessor(new EnvironmentAccessor());
                sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
                sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
                final ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
                if (conversionService != null) {
                    sec.setTypeConverter(new StandardTypeConverter(conversionService));
                }
                this.customizeEvaluationContext(sec);
                this.evaluationCache.put(evalContext, sec);
            }
            return expr.getValue(sec);
        }
        catch (Exception ex) {
            throw new BeanExpressionException("Expression parsing failed", ex);
        }
    }
    
    protected void customizeEvaluationContext(final StandardEvaluationContext evalContext) {
    }
}
