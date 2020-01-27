// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.StringUtils;
import org.springframework.util.ReflectionUtils;
import java.security.PrivilegedAction;
import org.springframework.beans.BeanUtils;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.springframework.beans.BeanInstantiationException;
import java.lang.reflect.Constructor;
import org.springframework.beans.factory.BeanFactory;
import java.lang.reflect.Method;

public class SimpleInstantiationStrategy implements InstantiationStrategy
{
    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod;
    
    public static Method getCurrentlyInvokedFactoryMethod() {
        return SimpleInstantiationStrategy.currentlyInvokedFactoryMethod.get();
    }
    
    @Override
    public Object instantiate(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner) {
        if (beanDefinition.getMethodOverrides().isEmpty()) {
            Constructor<?> constructorToUse;
            synchronized (beanDefinition.constructorArgumentLock) {
                constructorToUse = (Constructor<?>)beanDefinition.resolvedConstructorOrFactoryMethod;
                if (constructorToUse == null) {
                    final Class<?> clazz = beanDefinition.getBeanClass();
                    if (clazz.isInterface()) {
                        throw new BeanInstantiationException(clazz, "Specified class is an interface");
                    }
                    try {
                        if (System.getSecurityManager() != null) {
                            constructorToUse = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor<?>>)new PrivilegedExceptionAction<Constructor<?>>() {
                                @Override
                                public Constructor<?> run() throws Exception {
                                    return clazz.getDeclaredConstructor((Class<?>[])null);
                                }
                            });
                        }
                        else {
                            constructorToUse = clazz.getDeclaredConstructor((Class<?>[])null);
                        }
                        beanDefinition.resolvedConstructorOrFactoryMethod = constructorToUse;
                    }
                    catch (Exception ex) {
                        throw new BeanInstantiationException(clazz, "No default constructor found", ex);
                    }
                }
            }
            return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
        }
        return this.instantiateWithMethodInjection(beanDefinition, beanName, owner);
    }
    
    protected Object instantiateWithMethodInjection(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }
    
    @Override
    public Object instantiate(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner, final Constructor<?> ctor, final Object[] args) {
        if (beanDefinition.getMethodOverrides().isEmpty()) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        ReflectionUtils.makeAccessible(ctor);
                        return null;
                    }
                });
            }
            return BeanUtils.instantiateClass(ctor, args);
        }
        return this.instantiateWithMethodInjection(beanDefinition, beanName, owner, ctor, args);
    }
    
    protected Object instantiateWithMethodInjection(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner, final Constructor<?> ctor, final Object[] args) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }
    
    @Override
    public Object instantiate(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner, final Object factoryBean, final Method factoryMethod, final Object[] args) {
        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        ReflectionUtils.makeAccessible(factoryMethod);
                        return null;
                    }
                });
            }
            else {
                ReflectionUtils.makeAccessible(factoryMethod);
            }
            final Method priorInvokedFactoryMethod = SimpleInstantiationStrategy.currentlyInvokedFactoryMethod.get();
            try {
                SimpleInstantiationStrategy.currentlyInvokedFactoryMethod.set(factoryMethod);
                return factoryMethod.invoke(factoryBean, args);
            }
            finally {
                if (priorInvokedFactoryMethod != null) {
                    SimpleInstantiationStrategy.currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                }
                else {
                    SimpleInstantiationStrategy.currentlyInvokedFactoryMethod.remove();
                }
            }
        }
        catch (IllegalArgumentException ex2) {
            throw new BeanDefinitionStoreException("Illegal arguments to factory method [" + factoryMethod + "]; " + "args: " + StringUtils.arrayToCommaDelimitedString(args));
        }
        catch (IllegalAccessException ex3) {
            throw new BeanDefinitionStoreException("Cannot access factory method [" + factoryMethod + "]; is it public?");
        }
        catch (InvocationTargetException ex) {
            throw new BeanDefinitionStoreException("Factory method [" + factoryMethod + "] threw exception", ex.getTargetException());
        }
    }
    
    static {
        currentlyInvokedFactoryMethod = new ThreadLocal<Method>();
    }
}
