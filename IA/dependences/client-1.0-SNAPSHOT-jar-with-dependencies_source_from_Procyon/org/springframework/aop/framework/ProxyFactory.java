// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;

public class ProxyFactory extends ProxyCreatorSupport
{
    public ProxyFactory() {
    }
    
    public ProxyFactory(final Object target) {
        Assert.notNull(target, "Target object must not be null");
        this.setInterfaces(ClassUtils.getAllInterfaces(target));
        this.setTarget(target);
    }
    
    public ProxyFactory(final Class<?>... proxyInterfaces) {
        this.setInterfaces(proxyInterfaces);
    }
    
    public ProxyFactory(final Class<?> proxyInterface, final Interceptor interceptor) {
        this.addInterface(proxyInterface);
        this.addAdvice(interceptor);
    }
    
    public ProxyFactory(final Class<?> proxyInterface, final TargetSource targetSource) {
        this.addInterface(proxyInterface);
        this.setTargetSource(targetSource);
    }
    
    public Object getProxy() {
        return this.createAopProxy().getProxy();
    }
    
    public Object getProxy(final ClassLoader classLoader) {
        return this.createAopProxy().getProxy(classLoader);
    }
    
    public static <T> T getProxy(final Class<T> proxyInterface, final Interceptor interceptor) {
        return (T)new ProxyFactory(proxyInterface, interceptor).getProxy();
    }
    
    public static <T> T getProxy(final Class<T> proxyInterface, final TargetSource targetSource) {
        return (T)new ProxyFactory(proxyInterface, targetSource).getProxy();
    }
    
    public static Object getProxy(final TargetSource targetSource) {
        if (targetSource.getTargetClass() == null) {
            throw new IllegalArgumentException("Cannot create class proxy for TargetSource with null target class");
        }
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.setProxyTargetClass(true);
        return proxyFactory.getProxy();
    }
}
