// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import org.springframework.remoting.soap.SoapFaultException;

public class JaxWsSoapFaultException extends SoapFaultException
{
    public JaxWsSoapFaultException(final SOAPFaultException original) {
        super(original.getMessage(), (Throwable)original);
    }
    
    public final SOAPFault getFault() {
        return ((SOAPFaultException)this.getCause()).getFault();
    }
    
    @Override
    public String getFaultCode() {
        return this.getFault().getFaultCode();
    }
    
    @Override
    public QName getFaultCodeAsQName() {
        return this.getFault().getFaultCodeAsQName();
    }
    
    @Override
    public String getFaultString() {
        return this.getFault().getFaultString();
    }
    
    @Override
    public String getFaultActor() {
        return this.getFault().getFaultActor();
    }
}
