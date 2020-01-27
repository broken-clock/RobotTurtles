// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

public interface RemoteInvocationFactory
{
    RemoteInvocation createRemoteInvocation(final MethodInvocation p0);
}
