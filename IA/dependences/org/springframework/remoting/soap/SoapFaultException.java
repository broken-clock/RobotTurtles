// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.soap;

import javax.xml.namespace.QName;
import org.springframework.remoting.RemoteInvocationFailureException;

public abstract class SoapFaultException extends RemoteInvocationFailureException
{
    protected SoapFaultException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public abstract String getFaultCode();
    
    public abstract QName getFaultCodeAsQName();
    
    public abstract String getFaultString();
    
    public abstract String getFaultActor();
}
