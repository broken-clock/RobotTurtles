// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

public class HessianProxyFactoryBean extends HessianClientInterceptor implements FactoryBean<Object>
{
    private Object serviceProxy;
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
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
