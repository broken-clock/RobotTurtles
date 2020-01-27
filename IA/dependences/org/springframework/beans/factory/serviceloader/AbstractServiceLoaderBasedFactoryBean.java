// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public abstract class AbstractServiceLoaderBasedFactoryBean extends AbstractFactoryBean<Object> implements BeanClassLoaderAware
{
    private Class<?> serviceType;
    private ClassLoader beanClassLoader;
    
    public AbstractServiceLoaderBasedFactoryBean() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setServiceType(final Class<?> serviceType) {
        this.serviceType = serviceType;
    }
    
    public Class<?> getServiceType() {
        return this.serviceType;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    @Override
    protected Object createInstance() {
        Assert.notNull(this.getServiceType(), "Property 'serviceType' is required");
        return this.getObjectToExpose(ServiceLoader.load(this.getServiceType(), this.beanClassLoader));
    }
    
    protected abstract Object getObjectToExpose(final ServiceLoader<?> p0);
}
