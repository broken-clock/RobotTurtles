// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.serviceloader;

import java.util.Iterator;
import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class ServiceFactoryBean extends AbstractServiceLoaderBasedFactoryBean implements BeanClassLoaderAware
{
    @Override
    protected Object getObjectToExpose(final ServiceLoader<?> serviceLoader) {
        final Iterator<?> it = serviceLoader.iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("ServiceLoader could not find service for type [" + this.getServiceType() + "]");
        }
        return it.next();
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.getServiceType();
    }
}
