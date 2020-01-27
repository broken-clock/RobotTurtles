// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import org.apache.commons.logging.LogFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import java.rmi.registry.Registry;
import org.springframework.beans.factory.FactoryBean;

public class RmiRegistryFactoryBean implements FactoryBean<Registry>, InitializingBean, DisposableBean
{
    protected final Log logger;
    private String host;
    private int port;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    private boolean alwaysCreate;
    private boolean created;
    
    public RmiRegistryFactoryBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.port = 1099;
        this.alwaysCreate = false;
        this.created = false;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setClientSocketFactory(final RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }
    
    public void setServerSocketFactory(final RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }
    
    public void setAlwaysCreate(final boolean alwaysCreate) {
        this.alwaysCreate = alwaysCreate;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = (RMIServerSocketFactory)this.clientSocketFactory;
        }
        if ((this.clientSocketFactory != null && this.serverSocketFactory == null) || (this.clientSocketFactory == null && this.serverSocketFactory != null)) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        this.registry = this.getRegistry(this.host, this.port, this.clientSocketFactory, this.serverSocketFactory);
    }
    
    protected Registry getRegistry(final String registryHost, final int registryPort, final RMIClientSocketFactory clientSocketFactory, final RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (registryHost != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
            }
            final Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
            this.testRegistry(reg);
            return reg;
        }
        return this.getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
    }
    
    protected Registry getRegistry(final int registryPort, final RMIClientSocketFactory clientSocketFactory, final RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (clientSocketFactory != null) {
            if (this.alwaysCreate) {
                this.logger.info("Creating new RMI registry");
                this.created = true;
                return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Looking for RMI registry at port '" + registryPort + "', using custom socket factory");
            }
            synchronized (LocateRegistry.class) {
                try {
                    final Registry reg = LocateRegistry.getRegistry(null, registryPort, clientSocketFactory);
                    this.testRegistry(reg);
                    return reg;
                }
                catch (RemoteException ex) {
                    this.logger.debug("RMI registry access threw exception", ex);
                    this.logger.info("Could not detect RMI registry - creating new one");
                    this.created = true;
                    return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
                }
            }
        }
        return this.getRegistry(registryPort);
    }
    
    protected Registry getRegistry(final int registryPort) throws RemoteException {
        if (this.alwaysCreate) {
            this.logger.info("Creating new RMI registry");
            this.created = true;
            return LocateRegistry.createRegistry(registryPort);
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Looking for RMI registry at port '" + registryPort + "'");
        }
        synchronized (LocateRegistry.class) {
            try {
                final Registry reg = LocateRegistry.getRegistry(registryPort);
                this.testRegistry(reg);
                return reg;
            }
            catch (RemoteException ex) {
                this.logger.debug("RMI registry access threw exception", ex);
                this.logger.info("Could not detect RMI registry - creating new one");
                this.created = true;
                return LocateRegistry.createRegistry(registryPort);
            }
        }
    }
    
    protected void testRegistry(final Registry registry) throws RemoteException {
        registry.list();
    }
    
    @Override
    public Registry getObject() throws Exception {
        return this.registry;
    }
    
    @Override
    public Class<? extends Registry> getObjectType() {
        return (this.registry != null) ? this.registry.getClass() : Registry.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() throws RemoteException {
        if (this.created) {
            this.logger.info("Unexporting RMI registry");
            UnicastRemoteObject.unexportObject(this.registry, true);
        }
    }
}
