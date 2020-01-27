// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import javax.ejb.EJBObject;
import java.lang.reflect.InvocationTargetException;
import javax.ejb.CreateException;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.DisposableBean;

public class SimpleRemoteSlsbInvokerInterceptor extends AbstractRemoteSlsbInvokerInterceptor implements DisposableBean
{
    private boolean cacheSessionBean;
    private Object beanInstance;
    private final Object beanInstanceMonitor;
    
    public SimpleRemoteSlsbInvokerInterceptor() {
        this.cacheSessionBean = false;
        this.beanInstanceMonitor = new Object();
    }
    
    public void setCacheSessionBean(final boolean cacheSessionBean) {
        this.cacheSessionBean = cacheSessionBean;
    }
    
    @Override
    protected Object doInvoke(final MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            ejb = this.getSessionBeanInstance();
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, ejb);
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + this.getJndiName() + "]", ex);
        }
        catch (InvocationTargetException ex2) {
            final Throwable targetEx = ex2.getTargetException();
            if (targetEx instanceof RemoteException) {
                final RemoteException rex = (RemoteException)targetEx;
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, this.isConnectFailure(rex), this.getJndiName());
            }
            if (targetEx instanceof CreateException) {
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), targetEx, "Could not create remote EJB [" + this.getJndiName() + "]");
            }
            throw targetEx;
        }
        finally {
            if (ejb instanceof EJBObject) {
                this.releaseSessionBeanInstance((EJBObject)ejb);
            }
        }
    }
    
    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                if (this.beanInstance == null) {
                    this.beanInstance = this.newSessionBeanInstance();
                }
                return this.beanInstance;
            }
        }
        return this.newSessionBeanInstance();
    }
    
    protected void releaseSessionBeanInstance(final EJBObject ejb) {
        if (!this.cacheSessionBean) {
            this.removeSessionBeanInstance(ejb);
        }
    }
    
    @Override
    protected void refreshHome() throws NamingException {
        super.refreshHome();
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                this.beanInstance = null;
            }
        }
    }
    
    @Override
    public void destroy() {
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                if (this.beanInstance instanceof EJBObject) {
                    this.removeSessionBeanInstance((EJBObject)this.beanInstance);
                }
            }
        }
    }
}
