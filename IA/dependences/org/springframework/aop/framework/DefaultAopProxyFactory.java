// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.aop.SpringProxy;
import java.io.Serializable;

public class DefaultAopProxyFactory implements AopProxyFactory, Serializable
{
    @Override
    public AopProxy createAopProxy(final AdvisedSupport config) throws AopConfigException {
        if (!config.isOptimize() && !config.isProxyTargetClass() && !this.hasNoUserSuppliedProxyInterfaces(config)) {
            return new JdkDynamicAopProxy(config);
        }
        final Class<?> targetClass = config.getTargetClass();
        if (targetClass == null) {
            throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
        }
        if (targetClass.isInterface()) {
            return new JdkDynamicAopProxy(config);
        }
        return new ObjenesisCglibAopProxy(config);
    }
    
    private boolean hasNoUserSuppliedProxyInterfaces(final AdvisedSupport config) {
        final Class<?>[] interfaces = config.getProxiedInterfaces();
        return interfaces.length == 0 || (interfaces.length == 1 && SpringProxy.class.equals(interfaces[0]));
    }
}
