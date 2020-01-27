// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.apache.commons.logging.Log;
import org.aopalliance.intercept.MethodInvocation;

public class SimpleTraceInterceptor extends AbstractTraceInterceptor
{
    public SimpleTraceInterceptor() {
    }
    
    public SimpleTraceInterceptor(final boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }
    
    @Override
    protected Object invokeUnderTrace(final MethodInvocation invocation, final Log logger) throws Throwable {
        final String invocationDescription = this.getInvocationDescription(invocation);
        logger.trace("Entering " + invocationDescription);
        try {
            final Object rval = invocation.proceed();
            logger.trace("Exiting " + invocationDescription);
            return rval;
        }
        catch (Throwable ex) {
            logger.trace("Exception thrown in " + invocationDescription, ex);
            throw ex;
        }
    }
    
    protected String getInvocationDescription(final MethodInvocation invocation) {
        return "method '" + invocation.getMethod().getName() + "' of class [" + invocation.getThis().getClass().getName() + "]";
    }
}
