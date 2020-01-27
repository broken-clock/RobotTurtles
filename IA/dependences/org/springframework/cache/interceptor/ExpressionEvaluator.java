// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.expression.EvaluationContext;
import org.springframework.cache.Cache;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.DefaultParameterNameDiscoverer;
import java.lang.reflect.Method;
import org.springframework.expression.Expression;
import java.util.Map;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class ExpressionEvaluator
{
    public static final Object NO_RESULT;
    private final SpelExpressionParser parser;
    private final ParameterNameDiscoverer paramNameDiscoverer;
    private final Map<String, Expression> keyCache;
    private final Map<String, Expression> conditionCache;
    private final Map<String, Expression> unlessCache;
    private final Map<String, Method> targetMethodCache;
    
    ExpressionEvaluator() {
        this.parser = new SpelExpressionParser();
        this.paramNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.keyCache = new ConcurrentHashMap<String, Expression>(64);
        this.conditionCache = new ConcurrentHashMap<String, Expression>(64);
        this.unlessCache = new ConcurrentHashMap<String, Expression>(64);
        this.targetMethodCache = new ConcurrentHashMap<String, Method>(64);
    }
    
    public EvaluationContext createEvaluationContext(final Collection<? extends Cache> caches, final Method method, final Object[] args, final Object target, final Class<?> targetClass) {
        return this.createEvaluationContext(caches, method, args, target, targetClass, ExpressionEvaluator.NO_RESULT);
    }
    
    public EvaluationContext createEvaluationContext(final Collection<? extends Cache> caches, final Method method, final Object[] args, final Object target, final Class<?> targetClass, final Object result) {
        final CacheExpressionRootObject rootObject = new CacheExpressionRootObject(caches, method, args, target, targetClass);
        final LazyParamAwareEvaluationContext evaluationContext = new LazyParamAwareEvaluationContext(rootObject, this.paramNameDiscoverer, method, args, targetClass, this.targetMethodCache);
        if (result != ExpressionEvaluator.NO_RESULT) {
            evaluationContext.setVariable("result", result);
        }
        return evaluationContext;
    }
    
    public Object key(final String keyExpression, final Method method, final EvaluationContext evalContext) {
        return this.getExpression(this.keyCache, keyExpression, method).getValue(evalContext);
    }
    
    public boolean condition(final String conditionExpression, final Method method, final EvaluationContext evalContext) {
        return this.getExpression(this.conditionCache, conditionExpression, method).getValue(evalContext, Boolean.TYPE);
    }
    
    public boolean unless(final String unlessExpression, final Method method, final EvaluationContext evalContext) {
        return this.getExpression(this.unlessCache, unlessExpression, method).getValue(evalContext, Boolean.TYPE);
    }
    
    private Expression getExpression(final Map<String, Expression> cache, final String expression, final Method method) {
        final String key = this.toString(method, expression);
        Expression rtn = cache.get(key);
        if (rtn == null) {
            rtn = this.parser.parseExpression(expression);
            cache.put(key, rtn);
        }
        return rtn;
    }
    
    private String toString(final Method method, final String expression) {
        final StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append("#");
        sb.append(method.toString());
        sb.append("#");
        sb.append(expression);
        return sb.toString();
    }
    
    static {
        NO_RESULT = new Object();
    }
}
