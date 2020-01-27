// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.RemoteInvocation;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface RmiInvocationHandler extends Remote
{
    String getTargetInterfaceName() throws RemoteException;
    
    Object invoke(final RemoteInvocation p0) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
