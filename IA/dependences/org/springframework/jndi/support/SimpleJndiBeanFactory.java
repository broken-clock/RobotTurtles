// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi.support;

import javax.naming.NamingException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import javax.naming.NameNotFoundException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.BeansException;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.jndi.JndiLocatorSupport;

public class SimpleJndiBeanFactory extends JndiLocatorSupport implements BeanFactory
{
    private final Set<String> shareableResources;
    private final Map<String, Object> singletonObjects;
    private final Map<String, Class<?>> resourceTypes;
    
    public SimpleJndiBeanFactory() {
        this.shareableResources = new HashSet<String>();
        this.singletonObjects = new HashMap<String, Object>();
        this.resourceTypes = new HashMap<String, Class<?>>();
        this.setResourceRef(true);
    }
    
    public void setShareableResources(final String[] shareableResources) {
        this.shareableResources.addAll(Arrays.asList(shareableResources));
    }
    
    public void addShareableResource(final String shareableResource) {
        this.shareableResources.add(shareableResource);
    }
    
    @Override
    public Object getBean(final String name) throws BeansException {
        return this.getBean(name, Object.class);
    }
    
    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
        try {
            if (this.isSingleton(name)) {
                return (T)this.doGetSingleton(name, (Class<Object>)requiredType);
            }
            return this.lookup(name, requiredType);
        }
        catch (NameNotFoundException ex3) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
        catch (TypeMismatchNamingException ex) {
            throw new BeanNotOfRequiredTypeException(name, ex.getRequiredType(), ex.getActualType());
        }
        catch (NamingException ex2) {
            throw new BeanDefinitionStoreException("JNDI environment", name, "JNDI lookup failed", ex2);
        }
    }
    
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException {
        return this.getBean(requiredType.getSimpleName(), requiredType);
    }
    
    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments)");
        }
        return this.getBean(name);
    }
    
    @Override
    public boolean containsBean(final String name) {
        if (this.singletonObjects.containsKey(name) || this.resourceTypes.containsKey(name)) {
            return true;
        }
        try {
            this.doGetType(name);
            return true;
        }
        catch (NamingException ex) {
            return false;
        }
    }
    
    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        return this.shareableResources.contains(name);
    }
    
    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        return !this.shareableResources.contains(name);
    }
    
    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType) throws NoSuchBeanDefinitionException {
        final Class<?> type = this.getType(name);
        return targetType == null || (type != null && targetType.isAssignableFrom(type));
    }
    
    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        try {
            return this.doGetType(name);
        }
        catch (NameNotFoundException ex) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
        catch (NamingException ex2) {
            return null;
        }
    }
    
    @Override
    public String[] getAliases(final String name) {
        return new String[0];
    }
    
    private <T> T doGetSingleton(final String name, final Class<T> requiredType) throws NamingException {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(name)) {
                final T jndiObject = this.lookup(name, requiredType);
                this.singletonObjects.put(name, jndiObject);
                return jndiObject;
            }
            final Object jndiObject2 = this.singletonObjects.get(name);
            if (requiredType != null && !requiredType.isInstance(jndiObject2)) {
                throw new TypeMismatchNamingException(this.convertJndiName(name), requiredType, (jndiObject2 != null) ? jndiObject2.getClass() : null);
            }
            return (T)jndiObject2;
        }
    }
    
    private Class<?> doGetType(final String name) throws NamingException {
        if (this.isSingleton(name)) {
            final Object jndiObject = this.doGetSingleton(name, (Class<Object>)null);
            return (jndiObject != null) ? jndiObject.getClass() : null;
        }
        synchronized (this.resourceTypes) {
            if (this.resourceTypes.containsKey(name)) {
                return this.resourceTypes.get(name);
            }
            final Object jndiObject2 = this.lookup(name, (Class<Object>)null);
            final Class<?> type = (jndiObject2 != null) ? jndiObject2.getClass() : null;
            this.resourceTypes.put(name, type);
            return type;
        }
    }
}
