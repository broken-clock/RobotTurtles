// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;

public class DebugInterceptor extends SimpleTraceInterceptor
{
    private volatile long count;
    
    public DebugInterceptor() {
    }
    
    public DebugInterceptor(final boolean useDynamicLogger) {
        this.setUseDynamicLogger(useDynamicLogger);
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        synchronized (this) {
            ++this.count;
        }
        return super.invoke(invocation);
    }
    
    @Override
    protected String getInvocationDescription(final MethodInvocation invocation) {
        return invocation + "; count=" + this.count;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public synchronized void resetCount() {
        this.count = 0L;
    }
}
