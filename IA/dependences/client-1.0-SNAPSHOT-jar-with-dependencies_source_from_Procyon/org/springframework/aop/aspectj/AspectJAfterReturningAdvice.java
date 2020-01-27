// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.util.ClassUtils;
import java.lang.reflect.Type;
import org.springframework.util.TypeUtils;
import java.lang.reflect.Method;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.AfterReturningAdvice;

public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice implements AfterReturningAdvice, AfterAdvice
{
    public AspectJAfterReturningAdvice(final Method aspectJBeforeAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }
    
    @Override
    public boolean isBeforeAdvice() {
        return false;
    }
    
    @Override
    public boolean isAfterAdvice() {
        return true;
    }
    
    @Override
    public void setReturningName(final String name) {
        this.setReturningNameNoCheck(name);
    }
    
    @Override
    public void afterReturning(final Object returnValue, final Method method, final Object[] args, final Object target) throws Throwable {
        if (this.shouldInvokeOnReturnValueOf(method, returnValue)) {
            this.invokeAdviceMethod(this.getJoinPointMatch(), returnValue, null);
        }
    }
    
    private boolean shouldInvokeOnReturnValueOf(final Method method, final Object returnValue) {
        final Class<?> type = this.getDiscoveredReturningType();
        final Type genericType = this.getDiscoveredReturningGenericType();
        return this.matchesReturnValue(type, method, returnValue) && (genericType == null || genericType == type || TypeUtils.isAssignable(genericType, method.getGenericReturnType()));
    }
    
    private boolean matchesReturnValue(final Class<?> type, final Method method, final Object returnValue) {
        if (returnValue != null) {
            return ClassUtils.isAssignableValue(type, returnValue);
        }
        return (type.equals(Object.class) && method.getReturnType().equals(Void.TYPE)) || ClassUtils.isAssignable(type, method.getReturnType());
    }
}
