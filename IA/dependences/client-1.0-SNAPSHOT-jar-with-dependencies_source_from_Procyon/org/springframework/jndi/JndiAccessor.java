// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import java.util.Properties;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class JndiAccessor
{
    protected final Log logger;
    private JndiTemplate jndiTemplate;
    
    public JndiAccessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.jndiTemplate = new JndiTemplate();
    }
    
    public void setJndiTemplate(final JndiTemplate jndiTemplate) {
        this.jndiTemplate = ((jndiTemplate != null) ? jndiTemplate : new JndiTemplate());
    }
    
    public JndiTemplate getJndiTemplate() {
        return this.jndiTemplate;
    }
    
    public void setJndiEnvironment(final Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }
    
    public Properties getJndiEnvironment() {
        return this.jndiTemplate.getEnvironment();
    }
}
