// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import org.springframework.remoting.RemoteConnectFailureException;
import java.net.ConnectException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteProxyFailureException;
import java.lang.reflect.UndeclaredThrowableException;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.HessianException;
import com.caucho.hessian.client.HessianConnectionException;
import java.lang.reflect.InvocationTargetException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;
import java.net.MalformedURLException;
import org.springframework.remoting.RemoteLookupFailureException;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

public class HessianClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor
{
    private HessianProxyFactory proxyFactory;
    private Object hessianProxy;
    
    public HessianClientInterceptor() {
        this.proxyFactory = new HessianProxyFactory();
    }
    
    public void setProxyFactory(final HessianProxyFactory proxyFactory) {
        this.proxyFactory = ((proxyFactory != null) ? proxyFactory : new HessianProxyFactory());
    }
    
    public void setSerializerFactory(final SerializerFactory serializerFactory) {
        this.proxyFactory.setSerializerFactory(serializerFactory);
    }
    
    public void setSendCollectionType(final boolean sendCollectionType) {
        this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
    }
    
    public void setAllowNonSerializable(final boolean allowNonSerializable) {
        this.proxyFactory.getSerializerFactory().setAllowNonSerializable(allowNonSerializable);
    }
    
    public void setOverloadEnabled(final boolean overloadEnabled) {
        this.proxyFactory.setOverloadEnabled(overloadEnabled);
    }
    
    public void setUsername(final String username) {
        this.proxyFactory.setUser(username);
    }
    
    public void setPassword(final String password) {
        this.proxyFactory.setPassword(password);
    }
    
    public void setDebug(final boolean debug) {
        this.proxyFactory.setDebug(debug);
    }
    
    public void setChunkedPost(final boolean chunkedPost) {
        this.proxyFactory.setChunkedPost(chunkedPost);
    }
    
    public void setConnectionFactory(final HessianConnectionFactory connectionFactory) {
        this.proxyFactory.setConnectionFactory(connectionFactory);
    }
    
    public void setConnectTimeout(final long timeout) {
        this.proxyFactory.setConnectTimeout(timeout);
    }
    
    public void setReadTimeout(final long timeout) {
        this.proxyFactory.setReadTimeout(timeout);
    }
    
    public void setHessian2(final boolean hessian2) {
        this.proxyFactory.setHessian2Request(hessian2);
        this.proxyFactory.setHessian2Reply(hessian2);
    }
    
    public void setHessian2Request(final boolean hessian2) {
        this.proxyFactory.setHessian2Request(hessian2);
    }
    
    public void setHessian2Reply(final boolean hessian2) {
        this.proxyFactory.setHessian2Reply(hessian2);
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }
    
    public void prepare() throws RemoteLookupFailureException {
        try {
            this.hessianProxy = this.createHessianProxy(this.proxyFactory);
        }
        catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", ex);
        }
    }
    
    protected Object createHessianProxy(final HessianProxyFactory proxyFactory) throws MalformedURLException {
        Assert.notNull(this.getServiceInterface(), "'serviceInterface' is required");
        return proxyFactory.create((Class)this.getServiceInterface(), this.getServiceUrl(), this.getBeanClassLoader());
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (this.hessianProxy == null) {
            throw new IllegalStateException("HessianClientInterceptor is not properly initialized - invoke 'prepare' before attempting any operations");
        }
        final ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            return invocation.getMethod().invoke(this.hessianProxy, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof InvocationTargetException) {
                targetEx = ((InvocationTargetException)targetEx).getTargetException();
            }
            if (targetEx instanceof HessianConnectionException) {
                throw this.convertHessianAccessException(targetEx);
            }
            if (targetEx instanceof HessianException || targetEx instanceof HessianRuntimeException) {
                final Throwable cause = targetEx.getCause();
                throw this.convertHessianAccessException((cause != null) ? cause : targetEx);
            }
            if (targetEx instanceof UndeclaredThrowableException) {
                final UndeclaredThrowableException utex = (UndeclaredThrowableException)targetEx;
                throw this.convertHessianAccessException(utex.getUndeclaredThrowable());
            }
            throw targetEx;
        }
        catch (Throwable ex2) {
            throw new RemoteProxyFailureException("Failed to invoke Hessian proxy for remote service [" + this.getServiceUrl() + "]", ex2);
        }
        finally {
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }
    
    protected RemoteAccessException convertHessianAccessException(final Throwable ex) {
        if (ex instanceof HessianConnectionException || ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Cannot connect to Hessian remote service at [" + this.getServiceUrl() + "]", ex);
        }
        return new RemoteAccessException("Cannot access Hessian remote service at [" + this.getServiceUrl() + "]", ex);
    }
}
