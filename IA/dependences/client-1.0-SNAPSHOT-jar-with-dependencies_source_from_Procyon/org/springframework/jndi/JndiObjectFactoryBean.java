// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import java.lang.reflect.Method;
import javax.naming.Context;
import org.aopalliance.intercept.MethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.TargetSource;
import org.aopalliance.aop.Advice;
import java.lang.reflect.Modifier;
import org.springframework.aop.framework.ProxyFactory;
import javax.naming.NamingException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

public class JndiObjectFactoryBean extends JndiObjectLocator implements FactoryBean<Object>, BeanFactoryAware, BeanClassLoaderAware
{
    private Class<?>[] proxyInterfaces;
    private boolean lookupOnStartup;
    private boolean cache;
    private boolean exposeAccessContext;
    private Object defaultObject;
    private ConfigurableBeanFactory beanFactory;
    private ClassLoader beanClassLoader;
    private Object jndiObject;
    
    public JndiObjectFactoryBean() {
        this.lookupOnStartup = true;
        this.cache = true;
        this.exposeAccessContext = false;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setProxyInterface(final Class<?> proxyInterface) {
        this.proxyInterfaces = (Class<?>[])new Class[] { proxyInterface };
    }
    
    public void setProxyInterfaces(final Class<?>... proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }
    
    public void setLookupOnStartup(final boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }
    
    public void setCache(final boolean cache) {
        this.cache = cache;
    }
    
    public void setExposeAccessContext(final boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }
    
    public void setDefaultObject(final Object defaultObject) {
        this.defaultObject = defaultObject;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        }
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
        super.afterPropertiesSet();
        if (this.proxyInterfaces != null || !this.lookupOnStartup || !this.cache || this.exposeAccessContext) {
            if (this.defaultObject != null) {
                throw new IllegalArgumentException("'defaultObject' is not supported in combination with 'proxyInterface'");
            }
            this.jndiObject = createJndiObjectProxy(this);
        }
        else {
            if (this.defaultObject != null && this.getExpectedType() != null && !this.getExpectedType().isInstance(this.defaultObject)) {
                final TypeConverter converter = (this.beanFactory != null) ? this.beanFactory.getTypeConverter() : new SimpleTypeConverter();
                try {
                    this.defaultObject = converter.convertIfNecessary(this.defaultObject, this.getExpectedType());
                }
                catch (TypeMismatchException ex) {
                    throw new IllegalArgumentException("Default object [" + this.defaultObject + "] of type [" + this.defaultObject.getClass().getName() + "] is not of expected type [" + this.getExpectedType().getName() + "] and cannot be converted either", ex);
                }
            }
            this.jndiObject = this.lookupWithFallback();
        }
    }
    
    protected Object lookupWithFallback() throws NamingException {
        final ClassLoader originalClassLoader = ClassUtils.overrideThreadContextClassLoader(this.beanClassLoader);
        try {
            return this.lookup();
        }
        catch (TypeMismatchNamingException ex) {
            throw ex;
        }
        catch (NamingException ex2) {
            if (this.defaultObject != null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("JNDI lookup failed - returning specified default object instead", ex2);
                }
                else if (this.logger.isInfoEnabled()) {
                    this.logger.info("JNDI lookup failed - returning specified default object instead: " + ex2);
                }
                return this.defaultObject;
            }
            throw ex2;
        }
        finally {
            if (originalClassLoader != null) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }
    
    @Override
    public Object getObject() {
        return this.jndiObject;
    }
    
    @Override
    public Class<?> getObjectType() {
        if (this.proxyInterfaces != null) {
            if (this.proxyInterfaces.length == 1) {
                return this.proxyInterfaces[0];
            }
            if (this.proxyInterfaces.length > 1) {
                return this.createCompositeInterface(this.proxyInterfaces);
            }
        }
        if (this.jndiObject != null) {
            return this.jndiObject.getClass();
        }
        return this.getExpectedType();
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    protected Class<?> createCompositeInterface(final Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }
    
    private static class JndiObjectProxyFactory
    {
        private static Object createJndiObjectProxy(final JndiObjectFactoryBean jof) throws NamingException {
            final JndiObjectTargetSource targetSource = new JndiObjectTargetSource();
            targetSource.setJndiTemplate(jof.getJndiTemplate());
            targetSource.setJndiName(jof.getJndiName());
            targetSource.setExpectedType(jof.getExpectedType());
            targetSource.setResourceRef(jof.isResourceRef());
            targetSource.setLookupOnStartup(jof.lookupOnStartup);
            targetSource.setCache(jof.cache);
            targetSource.afterPropertiesSet();
            final ProxyFactory proxyFactory = new ProxyFactory();
            if (jof.proxyInterfaces != null) {
                proxyFactory.setInterfaces((Class<?>[])jof.proxyInterfaces);
            }
            else {
                final Class<?> targetClass = targetSource.getTargetClass();
                if (targetClass == null) {
                    throw new IllegalStateException("Cannot deactivate 'lookupOnStartup' without specifying a 'proxyInterface' or 'expectedType'");
                }
                final Class<?>[] allInterfacesForClass;
                final Class<?>[] ifcs = allInterfacesForClass = ClassUtils.getAllInterfacesForClass(targetClass, jof.beanClassLoader);
                for (final Class<?> ifc : allInterfacesForClass) {
                    if (Modifier.isPublic(ifc.getModifiers())) {
                        proxyFactory.addInterface(ifc);
                    }
                }
            }
            if (jof.exposeAccessContext) {
                proxyFactory.addAdvice(new JndiContextExposingInterceptor(jof.getJndiTemplate()));
            }
            proxyFactory.setTargetSource(targetSource);
            return proxyFactory.getProxy(jof.beanClassLoader);
        }
    }
    
    private static class JndiContextExposingInterceptor implements MethodInterceptor
    {
        private final JndiTemplate jndiTemplate;
        
        public JndiContextExposingInterceptor(final JndiTemplate jndiTemplate) {
            this.jndiTemplate = jndiTemplate;
        }
        
        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            final Context ctx = this.isEligible(invocation.getMethod()) ? this.jndiTemplate.getContext() : null;
            try {
                return invocation.proceed();
            }
            finally {
                this.jndiTemplate.releaseContext(ctx);
            }
        }
        
        protected boolean isEligible(final Method method) {
            return !Object.class.equals(method.getDeclaringClass());
        }
    }
}
