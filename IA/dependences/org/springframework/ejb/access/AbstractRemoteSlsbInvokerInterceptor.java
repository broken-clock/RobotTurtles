// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import javax.ejb.EJBObject;
import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;
import java.rmi.RemoteException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.aopalliance.intercept.MethodInvocation;
import javax.ejb.EJBHome;
import java.lang.reflect.Method;
import javax.naming.NamingException;
import org.springframework.remoting.RemoteLookupFailureException;
import javax.rmi.PortableRemoteObject;

public abstract class AbstractRemoteSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor
{
    private Class<?> homeInterface;
    private boolean refreshHomeOnConnectFailure;
    private volatile boolean homeAsComponent;
    
    public AbstractRemoteSlsbInvokerInterceptor() {
        this.refreshHomeOnConnectFailure = false;
        this.homeAsComponent = false;
    }
    
    public void setHomeInterface(final Class<?> homeInterface) {
        if (homeInterface != null && !homeInterface.isInterface()) {
            throw new IllegalArgumentException("Home interface class [" + homeInterface.getClass() + "] is not an interface");
        }
        this.homeInterface = homeInterface;
    }
    
    public void setRefreshHomeOnConnectFailure(final boolean refreshHomeOnConnectFailure) {
        this.refreshHomeOnConnectFailure = refreshHomeOnConnectFailure;
    }
    
    @Override
    protected boolean isHomeRefreshable() {
        return this.refreshHomeOnConnectFailure;
    }
    
    @Override
    protected Object lookup() throws NamingException {
        Object homeObject = super.lookup();
        if (this.homeInterface != null) {
            try {
                homeObject = PortableRemoteObject.narrow(homeObject, (Class)this.homeInterface);
            }
            catch (ClassCastException ex) {
                throw new RemoteLookupFailureException("Could not narrow EJB home stub to home interface [" + this.homeInterface.getName() + "]", ex);
            }
        }
        return homeObject;
    }
    
    @Override
    protected Method getCreateMethod(final Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }
    
    public Object invokeInContext(final MethodInvocation invocation) throws Throwable {
        try {
            return this.doInvoke(invocation);
        }
        catch (RemoteConnectFailureException ex) {
            return this.handleRemoteConnectFailure(invocation, ex);
        }
        catch (RemoteException ex2) {
            if (this.isConnectFailure(ex2)) {
                return this.handleRemoteConnectFailure(invocation, ex2);
            }
            throw ex2;
        }
    }
    
    protected boolean isConnectFailure(final RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }
    
    private Object handleRemoteConnectFailure(final MethodInvocation invocation, final Exception ex) throws Throwable {
        if (this.refreshHomeOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to remote EJB [" + this.getJndiName() + "] - retrying", ex);
            }
            else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to remote EJB [" + this.getJndiName() + "] - retrying");
            }
            return this.refreshAndRetry(invocation);
        }
        throw ex;
    }
    
    protected Object refreshAndRetry(final MethodInvocation invocation) throws Throwable {
        try {
            this.refreshHome();
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + this.getJndiName() + "]", ex);
        }
        return this.doInvoke(invocation);
    }
    
    protected abstract Object doInvoke(final MethodInvocation p0) throws Throwable;
    
    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to remote EJB");
        }
        final Object ejbInstance = this.create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to remote EJB: " + ejbInstance);
        }
        return ejbInstance;
    }
    
    protected void removeSessionBeanInstance(final EJBObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            }
            catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on remote EJB proxy", ex);
            }
        }
    }
}
