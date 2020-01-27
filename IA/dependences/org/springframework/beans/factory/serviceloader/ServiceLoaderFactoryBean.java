// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class ServiceLoaderFactoryBean extends AbstractServiceLoaderBasedFactoryBean implements BeanClassLoaderAware
{
    @Override
    protected Object getObjectToExpose(final ServiceLoader<?> serviceLoader) {
        return serviceLoader;
    }
    
    @Override
    public Class<?> getObjectType() {
        return ServiceLoader.class;
    }
}
