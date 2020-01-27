// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import org.springframework.remoting.RemoteConnectFailureException;
import java.net.ConnectException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteProxyFailureException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import com.caucho.burlap.client.BurlapRuntimeException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;
import java.net.MalformedURLException;
import org.springframework.remoting.RemoteLookupFailureException;
import com.caucho.burlap.client.BurlapProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

@Deprecated
public class BurlapClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor
{
    private BurlapProxyFactory proxyFactory;
    private Object burlapProxy;
    
    public BurlapClientInterceptor() {
        this.proxyFactory = new BurlapProxyFactory();
    }
    
    public void setProxyFactory(final BurlapProxyFactory proxyFactory) {
        this.proxyFactory = ((proxyFactory != null) ? proxyFactory : new BurlapProxyFactory());
    }
    
    public void setUsername(final String username) {
        this.proxyFactory.setUser(username);
    }
    
    public void setPassword(final String password) {
        this.proxyFactory.setPassword(password);
    }
    
    public void setOverloadEnabled(final boolean overloadEnabled) {
        this.proxyFactory.setOverloadEnabled(overloadEnabled);
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }
    
    public void prepare() throws RemoteLookupFailureException {
        try {
            this.burlapProxy = this.createBurlapProxy(this.proxyFactory);
        }
        catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", ex);
        }
    }
    
    protected Object createBurlapProxy(final BurlapProxyFactory proxyFactory) throws MalformedURLException {
        Assert.notNull(this.getServiceInterface(), "Property 'serviceInterface' is required");
        return proxyFactory.create((Class)this.getServiceInterface(), this.getServiceUrl());
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (this.burlapProxy == null) {
            throw new IllegalStateException("BurlapClientInterceptor is not properly initialized - invoke 'prepare' before attempting any operations");
        }
        final ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            return invocation.getMethod().invoke(this.burlapProxy, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            final Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof BurlapRuntimeException) {
                final Throwable cause = targetEx.getCause();
                throw this.convertBurlapAccessException((cause != null) ? cause : targetEx);
            }
            if (targetEx instanceof UndeclaredThrowableException) {
                final UndeclaredThrowableException utex = (UndeclaredThrowableException)targetEx;
                throw this.convertBurlapAccessException(utex.getUndeclaredThrowable());
            }
            throw targetEx;
        }
        catch (Throwable ex2) {
            throw new RemoteProxyFailureException("Failed to invoke Burlap proxy for remote service [" + this.getServiceUrl() + "]", ex2);
        }
        finally {
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }
    
    protected RemoteAccessException convertBurlapAccessException(final Throwable ex) {
        if (ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Cannot connect to Burlap remote service at [" + this.getServiceUrl() + "]", ex);
        }
        return new RemoteAccessException("Cannot access Burlap remote service at [" + this.getServiceUrl() + "]", ex);
    }
}
