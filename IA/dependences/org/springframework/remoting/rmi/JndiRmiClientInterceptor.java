// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.remoting.RemoteInvocationFailureException;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import javax.naming.Context;
import org.omg.CORBA.SystemException;
import java.rmi.RemoteException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.aopalliance.intercept.MethodInvocation;
import javax.rmi.PortableRemoteObject;
import org.springframework.remoting.RemoteLookupFailureException;
import javax.naming.NamingException;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.beans.factory.InitializingBean;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.jndi.JndiObjectLocator;

public class JndiRmiClientInterceptor extends JndiObjectLocator implements MethodInterceptor, InitializingBean
{
    private Class<?> serviceInterface;
    private RemoteInvocationFactory remoteInvocationFactory;
    private boolean lookupStubOnStartup;
    private boolean cacheStub;
    private boolean refreshStubOnConnectFailure;
    private boolean exposeAccessContext;
    private Object cachedStub;
    private final Object stubMonitor;
    
    public JndiRmiClientInterceptor() {
        this.remoteInvocationFactory = new DefaultRemoteInvocationFactory();
        this.lookupStubOnStartup = true;
        this.cacheStub = true;
        this.refreshStubOnConnectFailure = false;
        this.exposeAccessContext = false;
        this.stubMonitor = new Object();
    }
    
    public void setServiceInterface(final Class<?> serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }
    
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
    
    public void setRemoteInvocationFactory(final RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory;
    }
    
    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }
    
    public void setLookupStubOnStartup(final boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }
    
    public void setCacheStub(final boolean cacheStub) {
        this.cacheStub = cacheStub;
    }
    
    public void setRefreshStubOnConnectFailure(final boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }
    
    public void setExposeAccessContext(final boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        this.prepare();
    }
    
    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            final Object remoteObj = this.lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug("JNDI RMI object [" + this.getJndiName() + "] is an RMI invoker");
                }
                else if (this.getServiceInterface() != null) {
                    final boolean isImpl = this.getServiceInterface().isInstance(remoteObj);
                    this.logger.debug("Using service interface [" + this.getServiceInterface().getName() + "] for JNDI RMI object [" + this.getJndiName() + "] - " + (isImpl ? "" : "not ") + "directly implemented");
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }
    
    protected Object lookupStub() throws RemoteLookupFailureException {
        try {
            Object stub = this.lookup();
            if (this.getServiceInterface() != null && !(stub instanceof RmiInvocationHandler)) {
                try {
                    stub = PortableRemoteObject.narrow(stub, (Class)this.getServiceInterface());
                }
                catch (ClassCastException ex) {
                    throw new RemoteLookupFailureException("Could not narrow RMI stub to service interface [" + this.getServiceInterface().getName() + "]", ex);
                }
            }
            return stub;
        }
        catch (NamingException ex2) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + this.getJndiName() + "] failed", ex2);
        }
    }
    
    protected Object getStub() throws NamingException, RemoteLookupFailureException {
        if (!this.cacheStub || (this.lookupStubOnStartup && !this.refreshStubOnConnectFailure)) {
            return (this.cachedStub != null) ? this.cachedStub : this.lookupStub();
        }
        synchronized (this.stubMonitor) {
            if (this.cachedStub == null) {
                this.cachedStub = this.lookupStub();
            }
            return this.cachedStub;
        }
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Object stub;
        try {
            stub = this.getStub();
        }
        catch (NamingException ex) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + this.getJndiName() + "] failed", ex);
        }
        final Context ctx = this.exposeAccessContext ? this.getJndiTemplate().getContext() : null;
        try {
            return this.doInvoke(invocation, stub);
        }
        catch (RemoteConnectFailureException ex2) {
            return this.handleRemoteConnectFailure(invocation, ex2);
        }
        catch (RemoteException ex3) {
            if (this.isConnectFailure(ex3)) {
                return this.handleRemoteConnectFailure(invocation, ex3);
            }
            throw ex3;
        }
        catch (SystemException ex4) {
            if (this.isConnectFailure(ex4)) {
                return this.handleRemoteConnectFailure(invocation, (Exception)ex4);
            }
            throw ex4;
        }
        finally {
            this.getJndiTemplate().releaseContext(ctx);
        }
    }
    
    protected boolean isConnectFailure(final RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }
    
    protected boolean isConnectFailure(final SystemException ex) {
        return ex instanceof OBJECT_NOT_EXIST;
    }
    
    private Object handleRemoteConnectFailure(final MethodInvocation invocation, final Exception ex) throws Throwable {
        if (this.refreshStubOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to RMI service [" + this.getJndiName() + "] - retrying", ex);
            }
            else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to RMI service [" + this.getJndiName() + "] - retrying");
            }
            return this.refreshAndRetry(invocation);
        }
        throw ex;
    }
    
    protected Object refreshAndRetry(final MethodInvocation invocation) throws Throwable {
        final Object freshStub;
        synchronized (this.stubMonitor) {
            this.cachedStub = null;
            freshStub = this.lookupStub();
            if (this.cacheStub) {
                this.cachedStub = freshStub;
            }
        }
        return this.doInvoke(invocation, freshStub);
    }
    
    protected Object doInvoke(final MethodInvocation invocation, final Object stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return this.doInvoke(invocation, (RmiInvocationHandler)stub);
            }
            catch (RemoteException ex) {
                throw this.convertRmiAccessException(ex, invocation.getMethod());
            }
            catch (SystemException ex2) {
                throw this.convertCorbaAccessException(ex2, invocation.getMethod());
            }
            catch (InvocationTargetException ex3) {
                throw ex3.getTargetException();
            }
            catch (Throwable ex4) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + this.getJndiName() + "]", ex4);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        }
        catch (InvocationTargetException ex3) {
            final Throwable targetEx = ex3.getTargetException();
            if (targetEx instanceof RemoteException) {
                throw this.convertRmiAccessException((RemoteException)targetEx, invocation.getMethod());
            }
            if (targetEx instanceof SystemException) {
                throw this.convertCorbaAccessException((SystemException)targetEx, invocation.getMethod());
            }
            throw targetEx;
        }
    }
    
    protected Object doInvoke(final MethodInvocation methodInvocation, final RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + this.getJndiName() + "]";
        }
        return invocationHandler.invoke(this.createRemoteInvocation(methodInvocation));
    }
    
    protected RemoteInvocation createRemoteInvocation(final MethodInvocation methodInvocation) {
        return this.getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }
    
    private Exception convertRmiAccessException(final RemoteException ex, final Method method) {
        return RmiClientInterceptorUtils.convertRmiAccessException(method, ex, this.isConnectFailure(ex), this.getJndiName());
    }
    
    private Exception convertCorbaAccessException(final SystemException ex, final Method method) {
        if (ReflectionUtils.declaresException(method, RemoteException.class)) {
            return new RemoteException("Failed to access CORBA service [" + this.getJndiName() + "]", (Throwable)ex);
        }
        if (this.isConnectFailure(ex)) {
            return new RemoteConnectFailureException("Could not connect to CORBA service [" + this.getJndiName() + "]", (Throwable)ex);
        }
        return new RemoteAccessException("Could not access CORBA service [" + this.getJndiName() + "]", (Throwable)ex);
    }
}
