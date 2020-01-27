// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.access;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.access.BeanFactoryReference;

public class ContextBeanFactoryReference implements BeanFactoryReference
{
    private ApplicationContext applicationContext;
    
    public ContextBeanFactoryReference(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public BeanFactory getFactory() {
        if (this.applicationContext == null) {
            throw new IllegalStateException("ApplicationContext owned by this BeanFactoryReference has been released");
        }
        return this.applicationContext;
    }
    
    @Override
    public void release() {
        if (this.applicationContext != null) {
            final ApplicationContext savedCtx;
            synchronized (this) {
                savedCtx = this.applicationContext;
                this.applicationContext = null;
            }
            if (savedCtx != null && savedCtx instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext)savedCtx).close();
            }
        }
    }
}
