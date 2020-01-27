// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import javax.naming.NamingException;
import java.util.concurrent.Executor;
import java.util.Properties;
import org.springframework.jndi.JndiTemplate;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.beans.factory.InitializingBean;

public class DefaultManagedTaskExecutor extends ConcurrentTaskExecutor implements InitializingBean
{
    private JndiLocatorDelegate jndiLocator;
    private String jndiName;
    
    public DefaultManagedTaskExecutor() {
        this.jndiLocator = new JndiLocatorDelegate();
        this.jndiName = "java:comp/DefaultManagedExecutorService";
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
            this.setConcurrentExecutor(this.jndiLocator.lookup(this.jndiName, Executor.class));
        }
    }
}
