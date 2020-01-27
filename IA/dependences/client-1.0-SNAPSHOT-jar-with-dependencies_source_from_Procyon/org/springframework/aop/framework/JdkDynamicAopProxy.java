// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.apache.commons.logging.LogFactory;
import org.aopalliance.intercept.MethodInvocation;
import java.util.List;
import org.springframework.aop.TargetSource;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import java.lang.reflect.Method;
import org.springframework.aop.support.AopUtils;
import java.lang.reflect.Proxy;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable
{
    private static final long serialVersionUID = 5531744639992436476L;
    private static Log logger;
    private final AdvisedSupport advised;
    private boolean equalsDefined;
    private boolean hashCodeDefined;
    
    public JdkDynamicAopProxy(final AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
    }
    
    @Override
    public Object getProxy() {
        return this.getProxy(ClassUtils.getDefaultClassLoader());
    }
    
    @Override
    public Object getProxy(final ClassLoader classLoader) {
        if (JdkDynamicAopProxy.logger.isDebugEnabled()) {
            JdkDynamicAopProxy.logger.debug("Creating JDK dynamic proxy: target source is " + this.advised.getTargetSource());
        }
        final Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised);
        this.findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }
    
    private void findDefinedEqualsAndHashCodeMethods(final Class<?>[] proxiedInterfaces) {
        for (final Class<?> proxiedInterface : proxiedInterfaces) {
            final Method[] declaredMethods;
            final Method[] methods = declaredMethods = proxiedInterface.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                if (AopUtils.isEqualsMethod(method)) {
                    this.equalsDefined = true;
                }
                if (AopUtils.isHashCodeMethod(method)) {
                    this.hashCodeDefined = true;
                }
                if (this.equalsDefined && this.hashCodeDefined) {
                    return;
                }
            }
        }
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object oldProxy = null;
        boolean setProxyContext = false;
        final TargetSource targetSource = this.advised.targetSource;
        Class<?> targetClass = null;
        Object target = null;
        try {
            if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
                return this.equals(args[0]);
            }
            if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                return this.hashCode();
            }
            if (!this.advised.opaque && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
            }
            if (this.advised.exposeProxy) {
                oldProxy = AopContext.setCurrentProxy(proxy);
                setProxyContext = true;
            }
            target = targetSource.getTarget();
            if (target != null) {
                targetClass = target.getClass();
            }
            final List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            Object retVal;
            if (chain.isEmpty()) {
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
            }
            else {
                final MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
                retVal = invocation.proceed();
            }
            final Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target && returnType.isInstance(proxy) && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                retVal = proxy;
            }
            else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
            }
            return retVal;
        }
        finally {
            if (target != null && !targetSource.isStatic()) {
                targetSource.releaseTarget(target);
            }
            if (setProxyContext) {
                AopContext.setCurrentProxy(oldProxy);
            }
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        JdkDynamicAopProxy otherProxy;
        if (other instanceof JdkDynamicAopProxy) {
            otherProxy = (JdkDynamicAopProxy)other;
        }
        else {
            if (!Proxy.isProxyClass(other.getClass())) {
                return false;
            }
            final InvocationHandler ih = Proxy.getInvocationHandler(other);
            if (!(ih instanceof JdkDynamicAopProxy)) {
                return false;
            }
            otherProxy = (JdkDynamicAopProxy)ih;
        }
        return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
    }
    
    @Override
    public int hashCode() {
        return JdkDynamicAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }
    
    static {
        JdkDynamicAopProxy.logger = LogFactory.getLog(JdkDynamicAopProxy.class);
    }
}
