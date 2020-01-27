// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;

public abstract class JndiObjectLocator extends JndiLocatorSupport implements InitializingBean
{
    private String jndiName;
    private Class<?> expectedType;
    
    public void setJndiName(final String jndiName) {
        this.jndiName = jndiName;
    }
    
    public String getJndiName() {
        return this.jndiName;
    }
    
    public void setExpectedType(final Class<?> expectedType) {
        this.expectedType = expectedType;
    }
    
    public Class<?> getExpectedType() {
        return this.expectedType;
    }
    
    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
        if (!StringUtils.hasLength(this.getJndiName())) {
            throw new IllegalArgumentException("Property 'jndiName' is required");
        }
    }
    
    protected Object lookup() throws NamingException {
        return this.lookup(this.getJndiName(), this.getExpectedType());
    }
}
