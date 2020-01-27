// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.util.StopWatch;
import org.apache.commons.logging.Log;
import org.aopalliance.intercept.MethodInvocation;

public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor
{
    public PerformanceMonitorInterceptor() {
    }
    
    public PerformanceMonitorInterceptor(final boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }
    
    @Override
    protected Object invokeUnderTrace(final MethodInvocation invocation, final Log logger) throws Throwable {
        final String name = this.createInvocationTraceName(invocation);
        final StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            stopWatch.stop();
            logger.trace(stopWatch.shortSummary());
        }
    }
}
