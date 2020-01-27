// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import javax.naming.Context;
import org.aopalliance.intercept.MethodInvocation;
import javax.naming.NamingException;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.jndi.JndiObjectLocator;

public abstract class AbstractSlsbInvokerInterceptor extends JndiObjectLocator implements MethodInterceptor
{
    private boolean lookupHomeOnStartup;
    private boolean cacheHome;
    private boolean exposeAccessContext;
    private Object cachedHome;
    private Method createMethod;
    private final Object homeMonitor;
    
    public AbstractSlsbInvokerInterceptor() {
        this.lookupHomeOnStartup = true;
        this.cacheHome = true;
        this.exposeAccessContext = false;
        this.homeMonitor = new Object();
    }
    
    public void setLookupHomeOnStartup(final boolean lookupHomeOnStartup) {
        this.lookupHomeOnStartup = lookupHomeOnStartup;
    }
    
    public void setCacheHome(final boolean cacheHome) {
        this.cacheHome = cacheHome;
    }
    
    public void setExposeAccessContext(final boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupHomeOnStartup) {
            this.refreshHome();
        }
    }
    
    protected void refreshHome() throws NamingException {
        synchronized (this.homeMonitor) {
            final Object home = this.lookup();
            if (this.cacheHome) {
                this.cachedHome = home;
                this.createMethod = this.getCreateMethod(home);
            }
        }
    }
    
    protected Method getCreateMethod(final Object home) throws EjbAccessException {
        try {
            return home.getClass().getMethod("create", (Class<?>[])null);
        }
        catch (NoSuchMethodException ex) {
            throw new EjbAccessException("EJB home [" + home + "] has no no-arg create() method");
        }
    }
    
    protected Object getHome() throws NamingException {
        if (!this.cacheHome || (this.lookupHomeOnStartup && !this.isHomeRefreshable())) {
            return (this.cachedHome != null) ? this.cachedHome : this.lookup();
        }
        synchronized (this.homeMonitor) {
            if (this.cachedHome == null) {
                this.cachedHome = this.lookup();
                this.createMethod = this.getCreateMethod(this.cachedHome);
            }
            return this.cachedHome;
        }
    }
    
    protected boolean isHomeRefreshable() {
        return false;
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Context ctx = this.exposeAccessContext ? this.getJndiTemplate().getContext() : null;
        try {
            return this.invokeInContext(invocation);
        }
        finally {
            this.getJndiTemplate().releaseContext(ctx);
        }
    }
    
    protected abstract Object invokeInContext(final MethodInvocation p0) throws Throwable;
    
    protected Object create() throws NamingException, InvocationTargetException {
        try {
            final Object home = this.getHome();
            Method createMethodToUse = this.createMethod;
            if (createMethodToUse == null) {
                createMethodToUse = this.getCreateMethod(home);
            }
            if (createMethodToUse == null) {
                return home;
            }
            return createMethodToUse.invoke(home, (Object[])null);
        }
        catch (IllegalAccessException ex) {
            throw new EjbAccessException("Could not access EJB home create() method", ex);
        }
    }
}
