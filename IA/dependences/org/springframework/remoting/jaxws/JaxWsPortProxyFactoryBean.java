// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import org.aopalliance.aop.Advice;
import javax.xml.ws.BindingProvider;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

public class JaxWsPortProxyFactoryBean extends JaxWsPortClientInterceptor implements FactoryBean<Object>
{
    private Object serviceProxy;
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        final ProxyFactory pf = new ProxyFactory();
        pf.addInterface(this.getServiceInterface());
        pf.addInterface(BindingProvider.class);
        pf.addAdvice(this);
        this.serviceProxy = pf.getProxy(this.getBeanClassLoader());
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
