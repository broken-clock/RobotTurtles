// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import javax.naming.NamingException;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public class JndiRmiProxyFactoryBean extends JndiRmiClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware
{
    private ClassLoader beanClassLoader;
    private Object serviceProxy;
    
    public JndiRmiProxyFactoryBean() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.getServiceInterface() == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        this.serviceProxy = new ProxyFactory(this.getServiceInterface(), this).getProxy(this.beanClassLoader);
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
