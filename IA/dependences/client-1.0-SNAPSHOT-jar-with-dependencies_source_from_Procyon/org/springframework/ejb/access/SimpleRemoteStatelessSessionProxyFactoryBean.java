// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import javax.naming.NamingException;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public class SimpleRemoteStatelessSessionProxyFactoryBean extends SimpleRemoteSlsbInvokerInterceptor implements FactoryBean<Object>, BeanClassLoaderAware
{
    private Class<?> businessInterface;
    private ClassLoader beanClassLoader;
    private Object proxy;
    
    public SimpleRemoteStatelessSessionProxyFactoryBean() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setBusinessInterface(final Class<?> businessInterface) {
        this.businessInterface = businessInterface;
    }
    
    public Class<?> getBusinessInterface() {
        return this.businessInterface;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.businessInterface == null) {
            throw new IllegalArgumentException("businessInterface is required");
        }
        this.proxy = new ProxyFactory(this.businessInterface, this).getProxy(this.beanClassLoader);
    }
    
    @Override
    public Object getObject() {
        return this.proxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.businessInterface;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
