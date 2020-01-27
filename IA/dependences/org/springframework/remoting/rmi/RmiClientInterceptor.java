// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.io.IOException;
import java.net.URLConnection;
import org.springframework.aop.support.AopUtils;
import org.springframework.remoting.RemoteInvocationFailureException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.RemoteInvocationUtils;
import org.springframework.remoting.RemoteConnectFailureException;
import org.aopalliance.intercept.MethodInvocation;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.net.URLStreamHandler;
import java.net.URL;
import org.springframework.remoting.RemoteLookupFailureException;
import java.rmi.Remote;
import java.rmi.server.RMIClientSocketFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;

public class RmiClientInterceptor extends RemoteInvocationBasedAccessor implements MethodInterceptor
{
    private boolean lookupStubOnStartup;
    private boolean cacheStub;
    private boolean refreshStubOnConnectFailure;
    private RMIClientSocketFactory registryClientSocketFactory;
    private Remote cachedStub;
    private final Object stubMonitor;
    
    public RmiClientInterceptor() {
        this.lookupStubOnStartup = true;
        this.cacheStub = true;
        this.refreshStubOnConnectFailure = false;
        this.stubMonitor = new Object();
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
    
    public void setRegistryClientSocketFactory(final RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }
    
    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            final Remote remoteObj = this.lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug("RMI stub [" + this.getServiceUrl() + "] is an RMI invoker");
                }
                else if (this.getServiceInterface() != null) {
                    final boolean isImpl = this.getServiceInterface().isInstance(remoteObj);
                    this.logger.debug("Using service interface [" + this.getServiceInterface().getName() + "] for RMI stub [" + this.getServiceUrl() + "] - " + (isImpl ? "" : "not ") + "directly implemented");
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }
    
    protected Remote lookupStub() throws RemoteLookupFailureException {
        try {
            Remote stub = null;
            if (this.registryClientSocketFactory != null) {
                final URL url = new URL(null, this.getServiceUrl(), new DummyURLStreamHandler());
                final String protocol = url.getProtocol();
                if (protocol != null && !"rmi".equals(protocol)) {
                    throw new MalformedURLException("Invalid URL scheme '" + protocol + "'");
                }
                final String host = url.getHost();
                final int port = url.getPort();
                String name = url.getPath();
                if (name != null && name.startsWith("/")) {
                    name = name.substring(1);
                }
                final Registry registry = LocateRegistry.getRegistry(host, port, this.registryClientSocketFactory);
                stub = registry.lookup(name);
            }
            else {
                stub = Naming.lookup(this.getServiceUrl());
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located RMI stub with URL [" + this.getServiceUrl() + "]");
            }
            return stub;
        }
        catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", ex);
        }
        catch (NotBoundException ex2) {
            throw new RemoteLookupFailureException("Could not find RMI service [" + this.getServiceUrl() + "] in RMI registry", ex2);
        }
        catch (RemoteException ex3) {
            throw new RemoteLookupFailureException("Lookup of RMI stub failed", ex3);
        }
    }
    
    protected Remote getStub() throws RemoteLookupFailureException {
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
        final Remote stub = this.getStub();
        try {
            return this.doInvoke(invocation, stub);
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
        if (this.refreshStubOnConnectFailure) {
            final String msg = "Could not connect to RMI service [" + this.getServiceUrl() + "] - retrying";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex);
            }
            else if (this.logger.isWarnEnabled()) {
                this.logger.warn(msg);
            }
            return this.refreshAndRetry(invocation);
        }
        throw ex;
    }
    
    protected Object refreshAndRetry(final MethodInvocation invocation) throws Throwable {
        Remote freshStub = null;
        synchronized (this.stubMonitor) {
            this.cachedStub = null;
            freshStub = this.lookupStub();
            if (this.cacheStub) {
                this.cachedStub = freshStub;
            }
        }
        return this.doInvoke(invocation, freshStub);
    }
    
    protected Object doInvoke(final MethodInvocation invocation, final Remote stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return this.doInvoke(invocation, (RmiInvocationHandler)stub);
            }
            catch (RemoteException ex) {
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), ex, this.isConnectFailure(ex), this.getServiceUrl());
            }
            catch (InvocationTargetException ex2) {
                final Throwable exToThrow = ex2.getTargetException();
                RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
                throw exToThrow;
            }
            catch (Throwable ex3) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + this.getServiceUrl() + "]", ex3);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        }
        catch (InvocationTargetException ex2) {
            final Throwable targetEx = ex2.getTargetException();
            if (targetEx instanceof RemoteException) {
                final RemoteException rex = (RemoteException)targetEx;
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, this.isConnectFailure(rex), this.getServiceUrl());
            }
            throw targetEx;
        }
    }
    
    protected Object doInvoke(final MethodInvocation methodInvocation, final RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + this.getServiceUrl() + "]";
        }
        return invocationHandler.invoke(this.createRemoteInvocation(methodInvocation));
    }
    
    private static class DummyURLStreamHandler extends URLStreamHandler
    {
        @Override
        protected URLConnection openConnection(final URL url) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
