// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.RemoteInvocation;
import java.rmi.Remote;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

public abstract class RmiBasedExporter extends RemoteInvocationBasedExporter
{
    protected Remote getObjectToExport() {
        if (this.getService() instanceof Remote && (this.getServiceInterface() == null || Remote.class.isAssignableFrom(this.getServiceInterface()))) {
            return (Remote)this.getService();
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("RMI service [" + this.getService() + "] is an RMI invoker");
        }
        return new RmiInvocationWrapper(this.getProxyForService(), this);
    }
    
    @Override
    protected Object invoke(final RemoteInvocation invocation, final Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.invoke(invocation, targetObject);
    }
}
