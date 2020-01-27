// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;

public class MethodInvokingFactoryBean extends ArgumentConvertingMethodInvoker implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean
{
    private boolean singleton;
    private ClassLoader beanClassLoader;
    private ConfigurableBeanFactory beanFactory;
    private boolean initialized;
    private Object singletonObject;
    
    public MethodInvokingFactoryBean() {
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
    protected Class<?> resolveClassName(final String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        }
    }
    
    @Override
    protected TypeConverter getDefaultTypeConverter() {
        if (this.beanFactory != null) {
            return this.beanFactory.getTypeConverter();
        }
        return super.getDefaultTypeConverter();
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.prepare();
        if (this.singleton) {
            this.initialized = true;
            this.singletonObject = this.doInvoke();
        }
    }
    
    private Object doInvoke() throws Exception {
        try {
            return this.invoke();
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof Exception) {
                throw (Exception)ex.getTargetException();
            }
            if (ex.getTargetException() instanceof Error) {
                throw (Error)ex.getTargetException();
            }
            throw ex;
        }
    }
    
    @Override
    public Object getObject() throws Exception {
        if (!this.singleton) {
            return this.doInvoke();
        }
        if (!this.initialized) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.singletonObject;
    }
    
    @Override
    public Class<?> getObjectType() {
        if (!this.isPrepared()) {
            return null;
        }
        return this.getPreparedMethod().getReturnType();
    }
}
