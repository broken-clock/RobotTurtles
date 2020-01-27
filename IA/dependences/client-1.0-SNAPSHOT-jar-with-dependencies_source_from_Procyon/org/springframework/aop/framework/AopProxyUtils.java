// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import java.util.Arrays;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.aop.TargetClassAware;
import org.springframework.util.Assert;

public abstract class AopProxyUtils
{
    public static Class<?> ultimateTargetClass(final Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Object current = candidate;
        Class<?> result = null;
        while (current instanceof TargetClassAware) {
            result = ((TargetClassAware)current).getTargetClass();
            Object nested = null;
            if (current instanceof Advised) {
                final TargetSource targetSource = ((Advised)current).getTargetSource();
                if (targetSource instanceof SingletonTargetSource) {
                    nested = ((SingletonTargetSource)targetSource).getTarget();
                }
            }
            current = nested;
        }
        if (result == null) {
            result = (AopUtils.isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
        }
        return result;
    }
    
    public static Class<?>[] completeProxiedInterfaces(final AdvisedSupport advised) {
        Class<?>[] specifiedInterfaces = advised.getProxiedInterfaces();
        if (specifiedInterfaces.length == 0) {
            final Class<?> targetClass = advised.getTargetClass();
            if (targetClass != null && targetClass.isInterface()) {
                specifiedInterfaces = (Class<?>[])new Class[] { targetClass };
            }
        }
        final boolean addSpringProxy = !advised.isInterfaceProxied(SpringProxy.class);
        final boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Advised.class);
        int nonUserIfcCount = 0;
        if (addSpringProxy) {
            ++nonUserIfcCount;
        }
        if (addAdvised) {
            ++nonUserIfcCount;
        }
        final Class<?>[] proxiedInterfaces = (Class<?>[])new Class[specifiedInterfaces.length + nonUserIfcCount];
        System.arraycopy(specifiedInterfaces, 0, proxiedInterfaces, 0, specifiedInterfaces.length);
        if (addSpringProxy) {
            proxiedInterfaces[specifiedInterfaces.length] = SpringProxy.class;
        }
        if (addAdvised) {
            proxiedInterfaces[proxiedInterfaces.length - 1] = Advised.class;
        }
        return proxiedInterfaces;
    }
    
    public static Class<?>[] proxiedUserInterfaces(final Object proxy) {
        final Class<?>[] proxyInterfaces = proxy.getClass().getInterfaces();
        int nonUserIfcCount = 0;
        if (proxy instanceof SpringProxy) {
            ++nonUserIfcCount;
        }
        if (proxy instanceof Advised) {
            ++nonUserIfcCount;
        }
        final Class<?>[] userInterfaces = (Class<?>[])new Class[proxyInterfaces.length - nonUserIfcCount];
        System.arraycopy(proxyInterfaces, 0, userInterfaces, 0, userInterfaces.length);
        Assert.notEmpty(userInterfaces, "JDK proxy must implement one or more interfaces");
        return userInterfaces;
    }
    
    public static boolean equalsInProxy(final AdvisedSupport a, final AdvisedSupport b) {
        return a == b || (equalsProxiedInterfaces(a, b) && equalsAdvisors(a, b) && a.getTargetSource().equals(b.getTargetSource()));
    }
    
    public static boolean equalsProxiedInterfaces(final AdvisedSupport a, final AdvisedSupport b) {
        return Arrays.equals(a.getProxiedInterfaces(), b.getProxiedInterfaces());
    }
    
    public static boolean equalsAdvisors(final AdvisedSupport a, final AdvisedSupport b) {
        return Arrays.equals(a.getAdvisors(), b.getAdvisors());
    }
}
