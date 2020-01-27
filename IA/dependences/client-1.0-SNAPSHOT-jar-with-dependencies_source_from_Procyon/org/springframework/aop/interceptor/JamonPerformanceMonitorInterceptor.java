// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.logging.Log;
import org.aopalliance.intercept.MethodInvocation;

public class JamonPerformanceMonitorInterceptor extends AbstractMonitoringInterceptor
{
    private boolean trackAllInvocations;
    
    public JamonPerformanceMonitorInterceptor() {
        this.trackAllInvocations = false;
    }
    
    public JamonPerformanceMonitorInterceptor(final boolean useDynamicLogger) {
        this.trackAllInvocations = false;
        this.setUseDynamicLogger(useDynamicLogger);
    }
    
    public JamonPerformanceMonitorInterceptor(final boolean useDynamicLogger, final boolean trackAllInvocations) {
        this.trackAllInvocations = false;
        this.setUseDynamicLogger(useDynamicLogger);
        this.setTrackAllInvocations(trackAllInvocations);
    }
    
    public void setTrackAllInvocations(final boolean trackAllInvocations) {
        this.trackAllInvocations = trackAllInvocations;
    }
    
    @Override
    protected boolean isInterceptorEnabled(final MethodInvocation invocation, final Log logger) {
        return this.trackAllInvocations || this.isLogEnabled(logger);
    }
    
    @Override
    protected Object invokeUnderTrace(final MethodInvocation invocation, final Log logger) throws Throwable {
        final String name = this.createInvocationTraceName(invocation);
        final Monitor monitor = MonitorFactory.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            monitor.stop();
            if (!this.trackAllInvocations || this.isLogEnabled(logger)) {
                logger.trace("JAMon performance statistics for method [" + name + "]:\n" + monitor);
            }
        }
    }
}
