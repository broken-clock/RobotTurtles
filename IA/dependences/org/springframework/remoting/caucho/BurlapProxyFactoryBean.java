// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class BurlapProxyFactoryBean extends BurlapClientInterceptor implements FactoryBean<Object>
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
