// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import org.springframework.util.Assert;
import javax.naming.NamingException;

public abstract class JndiLocatorSupport extends JndiAccessor
{
    public static final String CONTAINER_PREFIX = "java:comp/env/";
    private boolean resourceRef;
    
    public JndiLocatorSupport() {
        this.resourceRef = false;
    }
    
    public void setResourceRef(final boolean resourceRef) {
        this.resourceRef = resourceRef;
    }
    
    public boolean isResourceRef() {
        return this.resourceRef;
    }
    
    protected Object lookup(final String jndiName) throws NamingException {
        return this.lookup(jndiName, (Class<Object>)null);
    }
    
    protected <T> T lookup(final String jndiName, final Class<T> requiredType) throws NamingException {
        Assert.notNull(jndiName, "'jndiName' must not be null");
        final String convertedName = this.convertJndiName(jndiName);
        T jndiObject;
        try {
            jndiObject = this.getJndiTemplate().lookup(convertedName, requiredType);
        }
        catch (NamingException ex) {
            if (convertedName.equals(jndiName)) {
                throw ex;
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Converted JNDI name [" + convertedName + "] not found - trying original name [" + jndiName + "]. " + ex);
            }
            jndiObject = this.getJndiTemplate().lookup(jndiName, requiredType);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located object with JNDI name [" + convertedName + "]");
        }
        return jndiObject;
    }
    
    protected String convertJndiName(String jndiName) {
        if (this.isResourceRef() && !jndiName.startsWith("java:comp/env/") && jndiName.indexOf(58) == -1) {
            jndiName = "java:comp/env/" + jndiName;
        }
        return jndiName;
    }
}
