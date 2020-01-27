// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.aop.support.AopUtils;
import org.springframework.util.ObjectUtils;
import java.util.Map;
import java.lang.reflect.Method;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;

class LazyParamAwareEvaluationContext extends StandardEvaluationContext
{
    private final ParameterNameDiscoverer paramDiscoverer;
    private final Method method;
    private final Object[] args;
    private final Class<?> targetClass;
    private final Map<String, Method> methodCache;
    private boolean paramLoaded;
    
    LazyParamAwareEvaluationContext(final Object rootObject, final ParameterNameDiscoverer paramDiscoverer, final Method method, final Object[] args, final Class<?> targetClass, final Map<String, Method> methodCache) {
        super(rootObject);
        this.paramLoaded = false;
        this.paramDiscoverer = paramDiscoverer;
        this.method = method;
        this.args = args;
        this.targetClass = targetClass;
        this.methodCache = methodCache;
    }
    
    @Override
    public Object lookupVariable(final String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.paramLoaded) {
            this.loadArgsAsVariables();
            this.paramLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }
    
    private void loadArgsAsVariables() {
        if (ObjectUtils.isEmpty(this.args)) {
            return;
        }
        final String methodKey = this.toString(this.method);
        Method targetMethod = this.methodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(this.method, this.targetClass);
            if (targetMethod == null) {
                targetMethod = this.method;
            }
            this.methodCache.put(methodKey, targetMethod);
        }
        for (int i = 0; i < this.args.length; ++i) {
            this.setVariable("a" + i, this.args[i]);
            this.setVariable("p" + i, this.args[i]);
        }
        final String[] parameterNames = this.paramDiscoverer.getParameterNames(targetMethod);
        if (parameterNames != null) {
            for (int j = 0; j < parameterNames.length; ++j) {
                this.setVariable(parameterNames[j], this.args[j]);
            }
        }
    }
    
    private String toString(final Method m) {
        final StringBuilder sb = new StringBuilder();
        sb.append(m.getDeclaringClass().getName());
        sb.append("#");
        sb.append(m.toString());
        return sb.toString();
    }
}
