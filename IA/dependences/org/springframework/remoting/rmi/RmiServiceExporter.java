// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean
{
    private String serviceName;
    private int servicePort;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    private String registryHost;
    private int registryPort;
    private RMIClientSocketFactory registryClientSocketFactory;
    private RMIServerSocketFactory registryServerSocketFactory;
    private boolean alwaysCreateRegistry;
    private boolean replaceExistingBinding;
    private Remote exportedObject;
    private boolean createdRegistry;
    
    public RmiServiceExporter() {
        this.servicePort = 0;
        this.registryPort = 1099;
        this.alwaysCreateRegistry = false;
        this.replaceExistingBinding = true;
        this.createdRegistry = false;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    public void setServicePort(final int servicePort) {
        this.servicePort = servicePort;
    }
    
    public void setClientSocketFactory(final RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }
    
    public void setServerSocketFactory(final RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }
    
    public void setRegistry(final Registry registry) {
        this.registry = registry;
    }
    
    public void setRegistryHost(final String registryHost) {
        this.registryHost = registryHost;
    }
    
    public void setRegistryPort(final int registryPort) {
        this.registryPort = registryPort;
    }
    
    public void setRegistryClientSocketFactory(final RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }
    
    public void setRegistryServerSocketFactory(final RMIServerSocketFactory registryServerSocketFactory) {
        this.registryServerSocketFactory = registryServerSocketFactory;
    }
    
    public void setAlwaysCreateRegistry(final boolean alwaysCreateRegistry) {
        this.alwaysCreateRegistry = alwaysCreateRegistry;
    }
    
    public void setReplaceExistingBinding(final boolean replaceExistingBinding) {
        this.replaceExistingBinding = replaceExistingBinding;
    }
    
    @Override
    public void afterPropertiesSet() throws RemoteException {
        this.prepare();
    }
    
    public void prepare() throws RemoteException {
        this.checkService();
        if (this.serviceName == null) {
            throw new IllegalArgumentException("Property 'serviceName' is required");
        }
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = (RMIServerSocketFactory)this.clientSocketFactory;
        }
        if ((this.clientSocketFactory != null && this.serverSocketFactory == null) || (this.clientSocketFactory == null && this.serverSocketFactory != null)) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
            this.registryServerSocketFactory = (RMIServerSocketFactory)this.registryClientSocketFactory;
        }
        if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
            throw new IllegalArgumentException("RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
        }
        this.createdRegistry = false;
        if (this.registry == null) {
            this.registry = this.getRegistry(this.registryHost, this.registryPort, this.registryClientSocketFactory, this.registryServerSocketFactory);
            this.createdRegistry = true;
        }
        this.exportedObject = this.getObjectToExport();
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry);
        }
        if (this.clientSocketFactory != null) {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort, this.clientSocketFactory, this.serverSocketFactory);
        }
        else {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort);
        }
        try {
            if (this.replaceExistingBinding) {
                this.registry.rebind(this.serviceName, this.exportedObject);
            }
            else {
                this.registry.bind(this.serviceName, this.exportedObject);
            }
        }
        catch (AlreadyBoundException ex) {
            this.unexportObjectSilently();
            throw new IllegalStateException("Already an RMI object bound for name '" + this.serviceName + "': " + ex.toString());
        }
        catch (RemoteException ex2) {
            this.unexportObjectSilently();
            throw ex2;
        }
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
            if (this.alwaysCreateRegistry) {
                this.logger.info("Creating new RMI registry");
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
                    return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
                }
            }
        }
        return this.getRegistry(registryPort);
    }
    
    protected Registry getRegistry(final int registryPort) throws RemoteException {
        if (this.alwaysCreateRegistry) {
            this.logger.info("Creating new RMI registry");
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
                return LocateRegistry.createRegistry(registryPort);
            }
        }
    }
    
    protected void testRegistry(final Registry registry) throws RemoteException {
        registry.list();
    }
    
    @Override
    public void destroy() throws RemoteException {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Unbinding RMI service '" + this.serviceName + "' from registry" + (this.createdRegistry ? (" at port '" + this.registryPort + "'") : ""));
        }
        try {
            this.registry.unbind(this.serviceName);
        }
        catch (NotBoundException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("RMI service '" + this.serviceName + "' is not bound to registry" + (this.createdRegistry ? (" at port '" + this.registryPort + "' anymore") : ""), ex);
            }
        }
        finally {
            this.unexportObjectSilently();
        }
    }
    
    private void unexportObjectSilently() {
        try {
            UnicastRemoteObject.unexportObject(this.exportedObject, true);
        }
        catch (NoSuchObjectException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("RMI object for service '" + this.serviceName + "' isn't exported anymore", ex);
            }
        }
    }
}
