// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.core.NamedThreadLocal;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import java.io.Serializable;
import org.springframework.core.Ordered;
import org.aopalliance.intercept.MethodInterceptor;

public class ExposeInvocationInterceptor implements MethodInterceptor, Ordered, Serializable
{
    public static final ExposeInvocationInterceptor INSTANCE;
    public static final Advisor ADVISOR;
    private static final ThreadLocal<MethodInvocation> invocation;
    
    public static MethodInvocation currentInvocation() throws IllegalStateException {
        final MethodInvocation mi = ExposeInvocationInterceptor.invocation.get();
        if (mi == null) {
            throw new IllegalStateException("No MethodInvocation found: Check that an AOP invocation is in progress, and that the ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor!");
        }
        return mi;
    }
    
    private ExposeInvocationInterceptor() {
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        final MethodInvocation oldInvocation = ExposeInvocationInterceptor.invocation.get();
        ExposeInvocationInterceptor.invocation.set(mi);
        try {
            return mi.proceed();
        }
        finally {
            ExposeInvocationInterceptor.invocation.set(oldInvocation);
        }
    }
    
    @Override
    public int getOrder() {
        return -2147483647;
    }
    
    private Object readResolve() {
        return ExposeInvocationInterceptor.INSTANCE;
    }
    
    static {
        INSTANCE = new ExposeInvocationInterceptor();
        ADVISOR = new DefaultPointcutAdvisor(ExposeInvocationInterceptor.INSTANCE) {
            @Override
            public String toString() {
                return ExposeInvocationInterceptor.class.getName() + ".ADVISOR";
            }
        };
        invocation = new NamedThreadLocal<MethodInvocation>("Current AOP method invocation");
    }
}
