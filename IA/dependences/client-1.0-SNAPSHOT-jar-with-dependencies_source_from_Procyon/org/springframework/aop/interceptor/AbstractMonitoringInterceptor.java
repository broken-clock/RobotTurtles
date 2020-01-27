// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;

public abstract class AbstractMonitoringInterceptor extends AbstractTraceInterceptor
{
    private String prefix;
    private String suffix;
    private boolean logTargetClassInvocation;
    
    public AbstractMonitoringInterceptor() {
        this.prefix = "";
        this.suffix = "";
        this.logTargetClassInvocation = false;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = ((prefix != null) ? prefix : "");
    }
    
    protected String getPrefix() {
        return this.prefix;
    }
    
    public void setSuffix(final String suffix) {
        this.suffix = ((suffix != null) ? suffix : "");
    }
    
    protected String getSuffix() {
        return this.suffix;
    }
    
    public void setLogTargetClassInvocation(final boolean logTargetClassInvocation) {
        this.logTargetClassInvocation = logTargetClassInvocation;
    }
    
    protected String createInvocationTraceName(final MethodInvocation invocation) {
        final StringBuilder sb = new StringBuilder(this.getPrefix());
        final Method method = invocation.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        if (this.logTargetClassInvocation && clazz.isInstance(invocation.getThis())) {
            clazz = invocation.getThis().getClass();
        }
        sb.append(clazz.getName());
        sb.append('.').append(method.getName());
        sb.append(this.getSuffix());
        return sb.toString();
    }
}
