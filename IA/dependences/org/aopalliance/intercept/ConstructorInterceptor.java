// 
// Decompiled by Procyon v0.5.36
// 

package org.aopalliance.intercept;

public interface ConstructorInterceptor extends Interceptor
{
    Object construct(final ConstructorInvocation p0) throws Throwable;
}
