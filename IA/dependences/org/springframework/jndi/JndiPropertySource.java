// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.env.PropertySource;

public class JndiPropertySource extends PropertySource<JndiLocatorDelegate>
{
    public JndiPropertySource(final String name) {
        this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
    }
    
    public JndiPropertySource(final String name, final JndiLocatorDelegate jndiLocator) {
        super(name, jndiLocator);
    }
    
    @Override
    public Object getProperty(final String name) {
        try {
            final Object value = ((JndiLocatorDelegate)this.source).lookup(name);
            this.logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
            return value;
        }
        catch (NamingException ex) {
            this.logger.debug("JNDI lookup for name [" + name + "] threw NamingException " + "with message: " + ex.getMessage() + ". Returning null.");
            return null;
        }
    }
}
