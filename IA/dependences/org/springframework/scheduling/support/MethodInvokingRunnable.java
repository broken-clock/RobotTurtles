// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;

public class MethodInvokingRunnable extends ArgumentConvertingMethodInvoker implements Runnable, BeanClassLoaderAware, InitializingBean
{
    protected final Log logger;
    private ClassLoader beanClassLoader;
    
    public MethodInvokingRunnable() {
        this.logger = LogFactory.getLog(this.getClass());
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
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
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        this.prepare();
    }
    
    @Override
    public void run() {
        try {
            this.invoke();
        }
        catch (InvocationTargetException ex) {
            this.logger.error(this.getInvocationFailureMessage(), ex.getTargetException());
        }
        catch (Throwable ex2) {
            this.logger.error(this.getInvocationFailureMessage(), ex2);
        }
    }
    
    protected String getInvocationFailureMessage() {
        return "Invocation of method '" + this.getTargetMethod() + "' on target class [" + this.getTargetClass() + "] failed";
    }
}
