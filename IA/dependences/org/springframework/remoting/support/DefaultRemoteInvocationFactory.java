// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

public class DefaultRemoteInvocationFactory implements RemoteInvocationFactory
{
    @Override
    public RemoteInvocation createRemoteInvocation(final MethodInvocation methodInvocation) {
        return new RemoteInvocation(methodInvocation);
    }
}
