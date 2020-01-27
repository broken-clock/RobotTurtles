// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public class RmiProxyFactoryBean extends RmiClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware
{
    private Object serviceProxy;
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.getServiceInterface() == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        this.serviceProxy = new ProxyFactory(this.getServiceInterface(), this).getProxy(this.getBeanClassLoader());
    }
    
    @Override
    public Object getObject() {
        return this.serviceProxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.getServiceInterface();
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
