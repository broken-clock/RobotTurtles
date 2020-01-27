// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

public abstract class RemoteAccessor extends RemotingSupport
{
    private Class<?> serviceInterface;
    
    public void setServiceInterface(final Class<?> serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }
    
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
}
