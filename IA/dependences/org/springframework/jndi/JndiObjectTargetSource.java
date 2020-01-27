// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.aop.TargetSource;

public class JndiObjectTargetSource extends JndiObjectLocator implements TargetSource
{
    private boolean lookupOnStartup;
    private boolean cache;
    private Object cachedObject;
    private Class<?> targetClass;
    
    public JndiObjectTargetSource() {
        this.lookupOnStartup = true;
        this.cache = true;
    }
    
    public void setLookupOnStartup(final boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }
    
    public void setCache(final boolean cache) {
        this.cache = cache;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupOnStartup) {
            final Object object = this.lookup();
            if (this.cache) {
                this.cachedObject = object;
            }
            else {
                this.targetClass = object.getClass();
            }
        }
    }
    
    @Override
    public Class<?> getTargetClass() {
        if (this.cachedObject != null) {
            return this.cachedObject.getClass();
        }
        if (this.targetClass != null) {
            return this.targetClass;
        }
        return this.getExpectedType();
    }
    
    @Override
    public boolean isStatic() {
        return this.cachedObject != null;
    }
    
    @Override
    public Object getTarget() {
        try {
            if (this.lookupOnStartup || !this.cache) {
                return (this.cachedObject != null) ? this.cachedObject : this.lookup();
            }
            synchronized (this) {
                if (this.cachedObject == null) {
                    this.cachedObject = this.lookup();
                }
                return this.cachedObject;
            }
        }
        catch (NamingException ex) {
            throw new JndiLookupFailureException("JndiObjectTargetSource failed to obtain new target object", ex);
        }
    }
    
    @Override
    public void releaseTarget(final Object target) {
    }
}
