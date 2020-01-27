// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.StringUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import java.util.Properties;
import java.lang.reflect.Constructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

public class ServiceLocatorFactoryBean implements FactoryBean<Object>, BeanFactoryAware, InitializingBean
{
    private Class<?> serviceLocatorInterface;
    private Constructor<Exception> serviceLocatorExceptionConstructor;
    private Properties serviceMappings;
    private ListableBeanFactory beanFactory;
    private Object proxy;
    
    public void setServiceLocatorInterface(final Class<?> interfaceType) {
        this.serviceLocatorInterface = interfaceType;
    }
    
    public void setServiceLocatorExceptionClass(final Class<? extends Exception> serviceLocatorExceptionClass) {
        if (serviceLocatorExceptionClass != null && !Exception.class.isAssignableFrom(serviceLocatorExceptionClass)) {
            throw new IllegalArgumentException("serviceLocatorException [" + serviceLocatorExceptionClass.getName() + "] is not a subclass of Exception");
        }
        this.serviceLocatorExceptionConstructor = this.determineServiceLocatorExceptionConstructor(serviceLocatorExceptionClass);
    }
    
    public void setServiceMappings(final Properties serviceMappings) {
        this.serviceMappings = serviceMappings;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new FatalBeanException("ServiceLocatorFactoryBean needs to run in a BeanFactory that is a ListableBeanFactory");
        }
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.serviceLocatorInterface == null) {
            throw new IllegalArgumentException("Property 'serviceLocatorInterface' is required");
        }
        this.proxy = Proxy.newProxyInstance(this.serviceLocatorInterface.getClassLoader(), new Class[] { this.serviceLocatorInterface }, new ServiceLocatorInvocationHandler());
    }
    
    protected Constructor<Exception> determineServiceLocatorExceptionConstructor(final Class<? extends Exception> exceptionClass) {
        try {
            return (Constructor<Exception>)exceptionClass.getConstructor(String.class, Throwable.class);
        }
        catch (NoSuchMethodException ex) {
            try {
                return (Constructor<Exception>)exceptionClass.getConstructor(Throwable.class);
            }
            catch (NoSuchMethodException ex2) {
                try {
                    return (Constructor<Exception>)exceptionClass.getConstructor(String.class);
                }
                catch (NoSuchMethodException ex3) {
                    throw new IllegalArgumentException("Service locator exception [" + exceptionClass.getName() + "] neither has a (String, Throwable) constructor nor a (String) constructor");
                }
            }
        }
    }
    
    protected Exception createServiceLocatorException(final Constructor<Exception> exceptionConstructor, final BeansException cause) {
        final Class<?>[] paramTypes = exceptionConstructor.getParameterTypes();
        final Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            if (paramTypes[i].equals(String.class)) {
                args[i] = cause.getMessage();
            }
            else if (paramTypes[i].isInstance(cause)) {
                args[i] = cause;
            }
        }
        return BeanUtils.instantiateClass(exceptionConstructor, args);
    }
    
    @Override
    public Object getObject() {
        return this.proxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.serviceLocatorInterface;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    private class ServiceLocatorInvocationHandler implements InvocationHandler
    {
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return proxy == args[0];
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return System.identityHashCode(proxy);
            }
            if (ReflectionUtils.isToStringMethod(method)) {
                return "Service locator: " + ServiceLocatorFactoryBean.this.serviceLocatorInterface.getName();
            }
            return this.invokeServiceLocatorMethod(method, args);
        }
        
        private Object invokeServiceLocatorMethod(final Method method, final Object[] args) throws Exception {
            final Class<?> serviceLocatorMethodReturnType = this.getServiceLocatorMethodReturnType(method);
            try {
                final String beanName = this.tryGetBeanName(args);
                if (StringUtils.hasLength(beanName)) {
                    return ServiceLocatorFactoryBean.this.beanFactory.getBean(beanName, serviceLocatorMethodReturnType);
                }
                return ServiceLocatorFactoryBean.this.beanFactory.getBean(serviceLocatorMethodReturnType);
            }
            catch (BeansException ex) {
                if (ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor != null) {
                    throw ServiceLocatorFactoryBean.this.createServiceLocatorException(ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor, ex);
                }
                throw ex;
            }
        }
        
        private String tryGetBeanName(final Object[] args) {
            String beanName = "";
            if (args != null && args.length == 1 && args[0] != null) {
                beanName = args[0].toString();
            }
            if (ServiceLocatorFactoryBean.this.serviceMappings != null) {
                final String mappedName = ServiceLocatorFactoryBean.this.serviceMappings.getProperty(beanName);
                if (mappedName != null) {
                    beanName = mappedName;
                }
            }
            return beanName;
        }
        
        private Class<?> getServiceLocatorMethodReturnType(final Method method) throws NoSuchMethodException {
            final Class<?>[] paramTypes = method.getParameterTypes();
            final Method interfaceMethod = ServiceLocatorFactoryBean.this.serviceLocatorInterface.getMethod(method.getName(), (Class[])paramTypes);
            final Class<?> serviceLocatorReturnType = interfaceMethod.getReturnType();
            if (paramTypes.length > 1 || Void.TYPE.equals(serviceLocatorReturnType)) {
                throw new UnsupportedOperationException("May only call methods with signature '<type> xxx()' or '<type> xxx(<idtype> id)' on factory interface, but tried to call: " + interfaceMethod);
            }
            return serviceLocatorReturnType;
        }
    }
}
