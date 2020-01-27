// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

public abstract class RemoteInvocationBasedAccessor extends UrlBasedRemoteAccessor
{
    private RemoteInvocationFactory remoteInvocationFactory;
    
    public RemoteInvocationBasedAccessor() {
        this.remoteInvocationFactory = new DefaultRemoteInvocationFactory();
    }
    
    public void setRemoteInvocationFactory(final RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = ((remoteInvocationFactory != null) ? remoteInvocationFactory : new DefaultRemoteInvocationFactory());
    }
    
    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }
    
    protected RemoteInvocation createRemoteInvocation(final MethodInvocation methodInvocation) {
        return this.getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }
    
    protected Object recreateRemoteInvocationResult(final RemoteInvocationResult result) throws Throwable {
        return result.recreate();
    }
}
