// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.util.Assert;

public class DefaultRemoteInvocationExecutor implements RemoteInvocationExecutor
{
    @Override
    public Object invoke(final RemoteInvocation invocation, final Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Assert.notNull(invocation, "RemoteInvocation must not be null");
        Assert.notNull(targetObject, "Target object must not be null");
        return invocation.invoke(targetObject);
    }
}
