// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;

public abstract class RemoteInvocationBasedExporter extends RemoteExporter
{
    private RemoteInvocationExecutor remoteInvocationExecutor;
    
    public RemoteInvocationBasedExporter() {
        this.remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();
    }
    
    public void setRemoteInvocationExecutor(final RemoteInvocationExecutor remoteInvocationExecutor) {
        this.remoteInvocationExecutor = remoteInvocationExecutor;
    }
    
    public RemoteInvocationExecutor getRemoteInvocationExecutor() {
        return this.remoteInvocationExecutor;
    }
    
    protected Object invoke(final RemoteInvocation invocation, final Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Executing " + invocation);
        }
        try {
            return this.getRemoteInvocationExecutor().invoke(invocation, targetObject);
        }
        catch (NoSuchMethodException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.warn("Could not find target method for " + invocation, ex);
            }
            throw ex;
        }
        catch (IllegalAccessException ex2) {
            if (this.logger.isDebugEnabled()) {
                this.logger.warn("Could not access target method for " + invocation, ex2);
            }
            throw ex2;
        }
        catch (InvocationTargetException ex3) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Target method failed for " + invocation, ex3.getTargetException());
            }
            throw ex3;
        }
    }
    
    protected RemoteInvocationResult invokeAndCreateResult(final RemoteInvocation invocation, final Object targetObject) {
        try {
            final Object value = this.invoke(invocation, targetObject);
            return new RemoteInvocationResult(value);
        }
        catch (Throwable ex) {
            return new RemoteInvocationResult(ex);
        }
    }
}
