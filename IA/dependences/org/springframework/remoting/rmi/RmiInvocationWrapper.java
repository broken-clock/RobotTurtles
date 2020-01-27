// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.util.Assert;

class RmiInvocationWrapper implements RmiInvocationHandler
{
    private final Object wrappedObject;
    private final RmiBasedExporter rmiExporter;
    
    public RmiInvocationWrapper(final Object wrappedObject, final RmiBasedExporter rmiExporter) {
        Assert.notNull(wrappedObject, "Object to wrap is required");
        Assert.notNull(rmiExporter, "RMI exporter is required");
        this.wrappedObject = wrappedObject;
        this.rmiExporter = rmiExporter;
    }
    
    @Override
    public String getTargetInterfaceName() {
        final Class<?> ifc = this.rmiExporter.getServiceInterface();
        return (ifc != null) ? ifc.getName() : null;
    }
    
    @Override
    public Object invoke(final RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return this.rmiExporter.invoke(invocation, this.wrappedObject);
    }
}
