// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.core.NamedThreadLocal;

public abstract class AopContext
{
    private static final ThreadLocal<Object> currentProxy;
    
    public static Object currentProxy() throws IllegalStateException {
        final Object proxy = AopContext.currentProxy.get();
        if (proxy == null) {
            throw new IllegalStateException("Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
        }
        return proxy;
    }
    
    static Object setCurrentProxy(final Object proxy) {
        final Object old = AopContext.currentProxy.get();
        if (proxy != null) {
            AopContext.currentProxy.set(proxy);
        }
        else {
            AopContext.currentProxy.remove();
        }
        return old;
    }
    
    static {
        currentProxy = new NamedThreadLocal<Object>("Current AOP proxy");
    }
}
