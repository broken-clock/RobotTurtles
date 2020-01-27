// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public abstract class AbstractFactoryBean<T> implements FactoryBean<T>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean
{
    protected final Log logger;
    private boolean singleton;
    private ClassLoader beanClassLoader;
    private BeanFactory beanFactory;
    private boolean initialized;
    private T singletonInstance;
    private T earlySingletonInstance;
    
    public AbstractFactoryBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.singleton = true;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.initialized = false;
    }
    
    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }
    
    @Override
    public boolean isSingleton() {
        return this.singleton;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    protected BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    protected TypeConverter getBeanTypeConverter() {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)beanFactory).getTypeConverter();
        }
        return new SimpleTypeConverter();
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.isSingleton()) {
            this.initialized = true;
            this.singletonInstance = this.createInstance();
            this.earlySingletonInstance = null;
        }
    }
    
    @Override
    public final T getObject() throws Exception {
        if (this.isSingleton()) {
            return this.initialized ? this.singletonInstance : this.getEarlySingletonInstance();
        }
        return this.createInstance();
    }
    
    private T getEarlySingletonInstance() throws Exception {
        final Class<?>[] ifcs = this.getEarlySingletonInterfaces();
        if (ifcs == null) {
            throw new FactoryBeanNotInitializedException(this.getClass().getName() + " does not support circular references");
        }
        if (this.earlySingletonInstance == null) {
            this.earlySingletonInstance = (T)Proxy.newProxyInstance(this.beanClassLoader, ifcs, new EarlySingletonInvocationHandler());
        }
        return this.earlySingletonInstance;
    }
    
    private T getSingletonInstance() throws IllegalStateException {
        if (!this.initialized) {
            throw new IllegalStateException("Singleton instance not initialized yet");
        }
        return this.singletonInstance;
    }
    
    @Override
    public void destroy() throws Exception {
        if (this.isSingleton()) {
            this.destroyInstance(this.singletonInstance);
        }
    }
    
    @Override
    public abstract Class<?> getObjectType();
    
    protected abstract T createInstance() throws Exception;
    
    protected Class<?>[] getEarlySingletonInterfaces() {
        final Class<?> type = this.getObjectType();
        return (Class<?>[])((type != null && type.isInterface()) ? new Class[] { type } : null);
    }
    
    protected void destroyInstance(final T instance) throws Exception {
    }
    
    private class EarlySingletonInvocationHandler implements InvocationHandler
    {
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return proxy == args[0];
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return System.identityHashCode(proxy);
            }
            if (!AbstractFactoryBean.this.initialized && ReflectionUtils.isToStringMethod(method)) {
                return "Early singleton proxy for interfaces " + ObjectUtils.nullSafeToString(AbstractFactoryBean.this.getEarlySingletonInterfaces());
            }
            try {
                return method.invoke(AbstractFactoryBean.this.getSingletonInstance(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
