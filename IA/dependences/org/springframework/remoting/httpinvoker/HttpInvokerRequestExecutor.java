// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.RemoteInvocation;

public interface HttpInvokerRequestExecutor
{
    RemoteInvocationResult executeRequest(final HttpInvokerClientConfiguration p0, final RemoteInvocation p1) throws Exception;
}
