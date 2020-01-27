// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.COMM_FAILURE;
import java.net.SocketException;
import java.rmi.StubNotFoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.UnknownHostException;
import java.rmi.ConnectIOException;
import java.rmi.ConnectException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.util.ReflectionUtils;
import java.rmi.RemoteException;
import java.lang.reflect.Method;
import org.springframework.remoting.RemoteProxyFailureException;
import java.lang.reflect.InvocationTargetException;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

public abstract class RmiClientInterceptorUtils
{
    private static final Log logger;
    
    public static Object invokeRemoteMethod(final MethodInvocation invocation, final Object stub) throws InvocationTargetException {
        final Method method = invocation.getMethod();
        try {
            if (method.getDeclaringClass().isInstance(stub)) {
                return method.invoke(stub, invocation.getArguments());
            }
            final Method stubMethod = stub.getClass().getMethod(method.getName(), method.getParameterTypes());
            return stubMethod.invoke(stub, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            throw ex;
        }
        catch (NoSuchMethodException ex2) {
            throw new RemoteProxyFailureException("No matching RMI stub method found for: " + method, ex2);
        }
        catch (Throwable ex3) {
            throw new RemoteProxyFailureException("Invocation of RMI stub method failed: " + method, ex3);
        }
    }
    
    public static Exception convertRmiAccessException(final Method method, final Throwable ex, final String message) {
        if (RmiClientInterceptorUtils.logger.isDebugEnabled()) {
            RmiClientInterceptorUtils.logger.debug(message, ex);
        }
        if (ReflectionUtils.declaresException(method, RemoteException.class)) {
            return new RemoteException(message, ex);
        }
        return new RemoteAccessException(message, ex);
    }
    
    public static Exception convertRmiAccessException(final Method method, final RemoteException ex, final String serviceName) {
        return convertRmiAccessException(method, ex, isConnectFailure(ex), serviceName);
    }
    
    public static Exception convertRmiAccessException(final Method method, final RemoteException ex, final boolean isConnectFailure, final String serviceName) {
        if (RmiClientInterceptorUtils.logger.isDebugEnabled()) {
            RmiClientInterceptorUtils.logger.debug("Remote service [" + serviceName + "] threw exception", ex);
        }
        if (ReflectionUtils.declaresException(method, ex.getClass())) {
            return ex;
        }
        if (isConnectFailure) {
            return new RemoteConnectFailureException("Could not connect to remote service [" + serviceName + "]", ex);
        }
        return new RemoteAccessException("Could not access remote service [" + serviceName + "]", ex);
    }
    
    public static boolean isConnectFailure(final RemoteException ex) {
        return ex instanceof ConnectException || ex instanceof ConnectIOException || ex instanceof UnknownHostException || ex instanceof NoSuchObjectException || ex instanceof StubNotFoundException || ex.getCause() instanceof SocketException || isCorbaConnectFailure(ex.getCause());
    }
    
    private static boolean isCorbaConnectFailure(final Throwable ex) {
        return (ex instanceof COMM_FAILURE || ex instanceof NO_RESPONSE) && ((SystemException)ex).completed == CompletionStatus.COMPLETED_NO;
    }
    
    static {
        logger = LogFactory.getLog(RmiClientInterceptorUtils.class);
    }
}
