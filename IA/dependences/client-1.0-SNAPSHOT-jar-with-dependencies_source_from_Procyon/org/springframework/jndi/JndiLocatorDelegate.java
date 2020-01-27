// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiLocatorDelegate extends JndiLocatorSupport
{
    public Object lookup(final String jndiName) throws NamingException {
        return super.lookup(jndiName);
    }
    
    public <T> T lookup(final String jndiName, final Class<T> requiredType) throws NamingException {
        return super.lookup(jndiName, requiredType);
    }
    
    public static JndiLocatorDelegate createDefaultResourceRefLocator() {
        final JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
        jndiLocator.setResourceRef(true);
        return jndiLocator;
    }
    
    public static boolean isDefaultJndiEnvironmentAvailable() {
        try {
            new InitialContext();
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}
