// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;

public interface RemoteInvocationExecutor
{
    Object invoke(final RemoteInvocation p0, final Object p1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
