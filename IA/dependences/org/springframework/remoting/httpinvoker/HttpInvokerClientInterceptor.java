// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import java.io.InvalidClassException;
import org.springframework.remoting.RemoteConnectFailureException;
import java.net.ConnectException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;

public class HttpInvokerClientInterceptor extends RemoteInvocationBasedAccessor implements MethodInterceptor, HttpInvokerClientConfiguration
{
    private String codebaseUrl;
    private HttpInvokerRequestExecutor httpInvokerRequestExecutor;
    
    public void setCodebaseUrl(final String codebaseUrl) {
        this.codebaseUrl = codebaseUrl;
    }
    
    @Override
    public String getCodebaseUrl() {
        return this.codebaseUrl;
    }
    
    public void setHttpInvokerRequestExecutor(final HttpInvokerRequestExecutor httpInvokerRequestExecutor) {
        this.httpInvokerRequestExecutor = httpInvokerRequestExecutor;
    }
    
    public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
        if (this.httpInvokerRequestExecutor == null) {
            final SimpleHttpInvokerRequestExecutor executor = new SimpleHttpInvokerRequestExecutor();
            executor.setBeanClassLoader(this.getBeanClassLoader());
            this.httpInvokerRequestExecutor = executor;
        }
        return this.httpInvokerRequestExecutor;
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.getHttpInvokerRequestExecutor();
    }
    
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "HTTP invoker proxy for service URL [" + this.getServiceUrl() + "]";
        }
        final RemoteInvocation invocation = this.createRemoteInvocation(methodInvocation);
        RemoteInvocationResult result = null;
        try {
            result = this.executeRequest(invocation, methodInvocation);
        }
        catch (Throwable ex) {
            throw this.convertHttpInvokerAccessException(ex);
        }
        try {
            return this.recreateRemoteInvocationResult(result);
        }
        catch (Throwable ex) {
            if (result.hasInvocationTargetException()) {
                throw ex;
            }
            throw new RemoteInvocationFailureException("Invocation of method [" + methodInvocation.getMethod() + "] failed in HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
        }
    }
    
    protected RemoteInvocationResult executeRequest(final RemoteInvocation invocation, final MethodInvocation originalInvocation) throws Exception {
        return this.executeRequest(invocation);
    }
    
    protected RemoteInvocationResult executeRequest(final RemoteInvocation invocation) throws Exception {
        return this.getHttpInvokerRequestExecutor().executeRequest(this, invocation);
    }
    
    protected RemoteAccessException convertHttpInvokerAccessException(final Throwable ex) {
        if (ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Could not connect to HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
        }
        if (ex instanceof ClassNotFoundException || ex instanceof NoClassDefFoundError || ex instanceof InvalidClassException) {
            return new RemoteAccessException("Could not deserialize result from HTTP invoker remote service [" + this.getServiceUrl() + "]", ex);
        }
        return new RemoteAccessException("Could not access HTTP invoker remote service at [" + this.getServiceUrl() + "]", ex);
    }
}
