// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import java.security.AccessControlContext;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.springframework.beans.factory.FactoryBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry
{
    private final Map<String, Object> factoryBeanObjectCache;
    
    public FactoryBeanRegistrySupport() {
        this.factoryBeanObjectCache = new ConcurrentHashMap<String, Object>(16);
    }
    
    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedAction<Class<?>>)new PrivilegedAction<Class<?>>() {
                    @Override
                    public Class<?> run() {
                        return (Class<?>)factoryBean.getObjectType();
                    }
                }, this.getAccessControlContext());
            }
            return factoryBean.getObjectType();
        }
        catch (Throwable ex) {
            this.logger.warn("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }
    
    protected Object getCachedObjectForFactoryBean(final String beanName) {
        final Object object = this.factoryBeanObjectCache.get(beanName);
        return (object != FactoryBeanRegistrySupport.NULL_OBJECT) ? object : null;
    }
    
    protected Object getObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName, final boolean shouldPostProcess) {
        if (factory.isSingleton() && this.containsSingleton(beanName)) {
            synchronized (this.getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = this.doGetObjectFromFactoryBean(factory, beanName, shouldPostProcess);
                    this.factoryBeanObjectCache.put(beanName, (object != null) ? object : FactoryBeanRegistrySupport.NULL_OBJECT);
                }
                return (object != FactoryBeanRegistrySupport.NULL_OBJECT) ? object : null;
            }
        }
        return this.doGetObjectFromFactoryBean(factory, beanName, shouldPostProcess);
    }
    
    private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName, final boolean shouldPostProcess) throws BeanCreationException {
        Object object;
        try {
            if (System.getSecurityManager() != null) {
                final AccessControlContext acc = this.getAccessControlContext();
                try {
                    object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws Exception {
                            return factory.getObject();
                        }
                    }, acc);
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            else {
                object = factory.getObject();
            }
        }
        catch (FactoryBeanNotInitializedException ex) {
            throw new BeanCurrentlyInCreationException(beanName, ex.toString());
        }
        catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex2);
        }
        if (object == null && this.isSingletonCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
        }
        if (object != null && shouldPostProcess) {
            try {
                object = this.postProcessObjectFromFactoryBean(object, beanName);
            }
            catch (Throwable ex2) {
                throw new BeanCreationException(beanName, "Post-processing of the FactoryBean's object failed", ex2);
            }
        }
        return object;
    }
    
    protected Object postProcessObjectFromFactoryBean(final Object object, final String beanName) throws BeansException {
        return object;
    }
    
    protected FactoryBean<?> getFactoryBean(final String beanName, final Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        return (FactoryBean<?>)beanInstance;
    }
    
    @Override
    protected void removeSingleton(final String beanName) {
        super.removeSingleton(beanName);
        this.factoryBeanObjectCache.remove(beanName);
    }
    
    protected AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }
}
