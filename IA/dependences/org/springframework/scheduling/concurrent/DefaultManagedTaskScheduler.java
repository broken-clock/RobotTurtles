// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import javax.naming.NamingException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Properties;
import org.springframework.jndi.JndiTemplate;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.beans.factory.InitializingBean;

public class DefaultManagedTaskScheduler extends ConcurrentTaskScheduler implements InitializingBean
{
    private JndiLocatorDelegate jndiLocator;
    private String jndiName;
    
    public DefaultManagedTaskScheduler() {
        this.jndiLocator = new JndiLocatorDelegate();
        this.jndiName = "java:comp/DefaultManagedScheduledExecutorService";
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
            final ScheduledExecutorService executor = this.jndiLocator.lookup(this.jndiName, ScheduledExecutorService.class);
            this.setConcurrentExecutor(executor);
            this.setScheduledExecutor(executor);
        }
    }
}
