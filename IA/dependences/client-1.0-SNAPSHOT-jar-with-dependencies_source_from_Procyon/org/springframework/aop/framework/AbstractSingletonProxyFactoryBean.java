// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.aop.TargetSource;
import org.springframework.util.ClassUtils;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public abstract class AbstractSingletonProxyFactoryBean extends ProxyConfig implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean
{
    private Object target;
    private Class<?>[] proxyInterfaces;
    private Object[] preInterceptors;
    private Object[] postInterceptors;
    private AdvisorAdapterRegistry advisorAdapterRegistry;
    private transient ClassLoader proxyClassLoader;
    private Object proxy;
    
    public AbstractSingletonProxyFactoryBean() {
        this.advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    }
    
    public void setTarget(final Object target) {
        this.target = target;
    }
    
    public void setProxyInterfaces(final Class<?>[] proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }
    
    public void setPreInterceptors(final Object[] preInterceptors) {
        this.preInterceptors = preInterceptors;
    }
    
    public void setPostInterceptors(final Object[] postInterceptors) {
        this.postInterceptors = postInterceptors;
    }
    
    public void setAdvisorAdapterRegistry(final AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }
    
    public void setProxyClassLoader(final ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = classLoader;
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.target == null) {
            throw new IllegalArgumentException("Property 'target' is required");
        }
        if (this.target instanceof String) {
            throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
        }
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
        }
        final ProxyFactory proxyFactory = new ProxyFactory();
        if (this.preInterceptors != null) {
            for (final Object interceptor : this.preInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(this.createMainInterceptor()));
        if (this.postInterceptors != null) {
            for (final Object interceptor : this.postInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.copyFrom(this);
        final TargetSource targetSource = this.createTargetSource(this.target);
        proxyFactory.setTargetSource(targetSource);
        if (this.proxyInterfaces != null) {
            proxyFactory.setInterfaces(this.proxyInterfaces);
        }
        else if (!this.isProxyTargetClass()) {
            proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(), this.proxyClassLoader));
        }
        this.proxy = proxyFactory.getProxy(this.proxyClassLoader);
    }
    
    protected TargetSource createTargetSource(final Object target) {
        if (target instanceof TargetSource) {
            return (TargetSource)target;
        }
        return new SingletonTargetSource(target);
    }
    
    @Override
    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        if (this.proxyInterfaces != null && this.proxyInterfaces.length == 1) {
            return this.proxyInterfaces[0];
        }
        if (this.target instanceof TargetSource) {
            return ((TargetSource)this.target).getTargetClass();
        }
        if (this.target != null) {
            return this.target.getClass();
        }
        return null;
    }
    
    @Override
    public final boolean isSingleton() {
        return true;
    }
    
    protected abstract Object createMainInterceptor();
}
