// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import javax.naming.NamingException;
import java.util.Properties;
import org.springframework.jndi.JndiTemplate;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.ThreadFactory;
import org.springframework.jndi.JndiLocatorDelegate;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;

public class DefaultManagedAwareThreadFactory extends CustomizableThreadFactory implements InitializingBean
{
    protected final Log logger;
    private JndiLocatorDelegate jndiLocator;
    private String jndiName;
    private ThreadFactory threadFactory;
    
    public DefaultManagedAwareThreadFactory() {
        this.logger = LogFactory.getLog(this.getClass());
        this.jndiLocator = new JndiLocatorDelegate();
        this.jndiName = "java:comp/DefaultManagedThreadFactory";
        this.threadFactory = this;
    }
    
    public void setJndiTemplate(final JndiTemplate jndiTemplate) {
        this.jndiLocator.setJndiTemplate(jndiTemplate);
    }
    
    public void setJndiEnvironment(final Properties jndiEnvironment) {
        this.jndiLocator.setJndiEnvironment(jndiEnvironment);
    }
    
    public void setResourceRef(final boolean resourceRef) {
        this.jndiLocator.setResourceRef(resourceRef);
    }
    
    public void setJndiName(final String jndiName) {
        this.jndiName = jndiName;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        if (this.jndiName != null) {
            try {
                this.threadFactory = this.jndiLocator.lookup(this.jndiName, ThreadFactory.class);
            }
            catch (NamingException ex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to find [java:comp/DefaultManagedThreadFactory] in JNDI", ex);
                }
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Could not find default managed thread factory in JNDI - proceeding with default local thread factory");
                }
            }
        }
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        return this.threadFactory.newThread(runnable);
    }
}
