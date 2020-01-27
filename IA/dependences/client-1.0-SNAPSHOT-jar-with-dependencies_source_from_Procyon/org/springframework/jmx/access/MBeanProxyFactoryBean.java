// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.springframework.jmx.MBeanServerNotFoundException;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public class MBeanProxyFactoryBean extends MBeanClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean
{
    private Class<?> proxyInterface;
    private ClassLoader beanClassLoader;
    private Object mbeanProxy;
    
    public MBeanProxyFactoryBean() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setProxyInterface(final Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws MBeanServerNotFoundException, MBeanInfoRetrievalException {
        super.afterPropertiesSet();
        if (this.proxyInterface == null) {
            this.proxyInterface = this.getManagementInterface();
            if (this.proxyInterface == null) {
                throw new IllegalArgumentException("Property 'proxyInterface' or 'managementInterface' is required");
            }
        }
        else if (this.getManagementInterface() == null) {
            this.setManagementInterface(this.proxyInterface);
        }
        this.mbeanProxy = new ProxyFactory(this.proxyInterface, this).getProxy(this.beanClassLoader);
    }
    
    @Override
    public Object getObject() {
        return this.mbeanProxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.proxyInterface;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
