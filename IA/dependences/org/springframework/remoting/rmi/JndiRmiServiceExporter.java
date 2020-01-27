// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.rmi.NoSuchObjectException;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import java.util.Properties;
import java.rmi.Remote;
import org.springframework.jndi.JndiTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class JndiRmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean
{
    private JndiTemplate jndiTemplate;
    private String jndiName;
    private Remote exportedObject;
    
    public JndiRmiServiceExporter() {
        this.jndiTemplate = new JndiTemplate();
    }
    
    public void setJndiTemplate(final JndiTemplate jndiTemplate) {
        this.jndiTemplate = ((jndiTemplate != null) ? jndiTemplate : new JndiTemplate());
    }
    
    public void setJndiEnvironment(final Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }
    
    public void setJndiName(final String jndiName) {
        this.jndiName = jndiName;
    }
    
    @Override
    public void afterPropertiesSet() throws NamingException, RemoteException {
        this.prepare();
    }
    
    public void prepare() throws NamingException, RemoteException {
        if (this.jndiName == null) {
            throw new IllegalArgumentException("Property 'jndiName' is required");
        }
        PortableRemoteObject.exportObject(this.exportedObject = this.getObjectToExport());
        this.rebind();
    }
    
    public void rebind() throws NamingException {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Binding RMI service to JNDI location [" + this.jndiName + "]");
        }
        this.jndiTemplate.rebind(this.jndiName, this.exportedObject);
    }
    
    @Override
    public void destroy() throws NamingException, NoSuchObjectException {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Unbinding RMI service from JNDI location [" + this.jndiName + "]");
        }
        this.jndiTemplate.unbind(this.jndiName);
        PortableRemoteObject.unexportObject(this.exportedObject);
    }
}
