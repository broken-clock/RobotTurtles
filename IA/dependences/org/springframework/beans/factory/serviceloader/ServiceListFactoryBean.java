// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.serviceloader;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class ServiceListFactoryBean extends AbstractServiceLoaderBasedFactoryBean implements BeanClassLoaderAware
{
    @Override
    protected Object getObjectToExpose(final ServiceLoader<?> serviceLoader) {
        final List<Object> result = new LinkedList<Object>();
        for (final Object loaderObject : serviceLoader) {
            result.add(loaderObject);
        }
        return result;
    }
    
    @Override
    public Class<?> getObjectType() {
        return List.class;
    }
}
