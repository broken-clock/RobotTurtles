// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.MessageSource;
import org.springframework.context.ApplicationContextException;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContextAware;

public abstract class ApplicationObjectSupport implements ApplicationContextAware
{
    protected final Log logger;
    private ApplicationContext applicationContext;
    private MessageSourceAccessor messageSourceAccessor;
    
    public ApplicationObjectSupport() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public final void setApplicationContext(final ApplicationContext context) throws BeansException {
        if (context == null && !this.isContextRequired()) {
            this.applicationContext = null;
            this.messageSourceAccessor = null;
        }
        else if (this.applicationContext == null) {
            if (!this.requiredContextClass().isInstance(context)) {
                throw new ApplicationContextException("Invalid application context: needs to be of type [" + this.requiredContextClass().getName() + "]");
            }
            this.applicationContext = context;
            this.messageSourceAccessor = new MessageSourceAccessor(context);
            this.initApplicationContext(context);
        }
        else if (this.applicationContext != context) {
            throw new ApplicationContextException("Cannot reinitialize with different application context: current one is [" + this.applicationContext + "], passed-in one is [" + context + "]");
        }
    }
    
    protected boolean isContextRequired() {
        return false;
    }
    
    protected Class<?> requiredContextClass() {
        return ApplicationContext.class;
    }
    
    protected void initApplicationContext(final ApplicationContext context) throws BeansException {
        this.initApplicationContext();
    }
    
    protected void initApplicationContext() throws BeansException {
    }
    
    public final ApplicationContext getApplicationContext() throws IllegalStateException {
        if (this.applicationContext == null && this.isContextRequired()) {
            throw new IllegalStateException("ApplicationObjectSupport instance [" + this + "] does not run in an ApplicationContext");
        }
        return this.applicationContext;
    }
    
    protected final MessageSourceAccessor getMessageSourceAccessor() throws IllegalStateException {
        if (this.messageSourceAccessor == null && this.isContextRequired()) {
            throw new IllegalStateException("ApplicationObjectSupport instance [" + this + "] does not run in an ApplicationContext");
        }
        return this.messageSourceAccessor;
    }
}
