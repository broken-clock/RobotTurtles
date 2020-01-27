// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanClassLoaderAware;

public abstract class RemotingSupport implements BeanClassLoaderAware
{
    protected final Log logger;
    private ClassLoader beanClassLoader;
    
    public RemotingSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    
    protected ClassLoader overrideThreadContextClassLoader() {
        return ClassUtils.overrideThreadContextClassLoader(this.getBeanClassLoader());
    }
    
    protected void resetThreadContextClassLoader(final ClassLoader original) {
        if (original != null) {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
