// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;

public abstract class AbstractTraceInterceptor implements MethodInterceptor, Serializable
{
    protected transient Log defaultLogger;
    private boolean hideProxyClassNames;
    
    public AbstractTraceInterceptor() {
        this.defaultLogger = LogFactory.getLog(this.getClass());
        this.hideProxyClassNames = false;
    }
    
    public void setUseDynamicLogger(final boolean useDynamicLogger) {
        this.defaultLogger = (useDynamicLogger ? null : LogFactory.getLog(this.getClass()));
    }
    
    public void setLoggerName(final String loggerName) {
        this.defaultLogger = LogFactory.getLog(loggerName);
    }
    
    public void setHideProxyClassNames(final boolean hideProxyClassNames) {
        this.hideProxyClassNames = hideProxyClassNames;
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Log logger = this.getLoggerForInvocation(invocation);
        if (this.isInterceptorEnabled(invocation, logger)) {
            return this.invokeUnderTrace(invocation, logger);
        }
        return invocation.proceed();
    }
    
    protected Log getLoggerForInvocation(final MethodInvocation invocation) {
        if (this.defaultLogger != null) {
            return this.defaultLogger;
        }
        final Object target = invocation.getThis();
        return LogFactory.getLog(this.getClassForLogging(target));
    }
    
    protected Class<?> getClassForLogging(final Object target) {
        return this.hideProxyClassNames ? AopUtils.getTargetClass(target) : target.getClass();
    }
    
    protected boolean isInterceptorEnabled(final MethodInvocation invocation, final Log logger) {
        return this.isLogEnabled(logger);
    }
    
    protected boolean isLogEnabled(final Log logger) {
        return logger.isTraceEnabled();
    }
    
    protected abstract Object invokeUnderTrace(final MethodInvocation p0, final Log p1) throws Throwable;
}
