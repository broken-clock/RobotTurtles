// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import javax.management.JMException;
import java.io.IOException;
import org.springframework.jmx.JmxException;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.MalformedObjectNameException;
import org.springframework.util.CollectionUtils;
import java.util.Properties;
import java.util.HashMap;
import javax.management.ObjectName;
import javax.management.remote.MBeanServerForwarder;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import javax.management.remote.JMXConnectorServer;
import org.springframework.beans.factory.FactoryBean;

public class ConnectorServerFactoryBean extends MBeanRegistrationSupport implements FactoryBean<JMXConnectorServer>, InitializingBean, DisposableBean
{
    public static final String DEFAULT_SERVICE_URL = "service:jmx:jmxmp://localhost:9875";
    private String serviceUrl;
    private Map<String, Object> environment;
    private MBeanServerForwarder forwarder;
    private ObjectName objectName;
    private boolean threaded;
    private boolean daemon;
    private JMXConnectorServer connectorServer;
    
    public ConnectorServerFactoryBean() {
        this.serviceUrl = "service:jmx:jmxmp://localhost:9875";
        this.environment = new HashMap<String, Object>();
        this.threaded = false;
        this.daemon = false;
    }
    
    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    public void setEnvironment(final Properties environment) {
        CollectionUtils.mergePropertiesIntoMap(environment, this.environment);
    }
    
    public void setEnvironmentMap(final Map<String, ?> environment) {
        if (environment != null) {
            this.environment.putAll(environment);
        }
    }
    
    public void setForwarder(final MBeanServerForwarder forwarder) {
        this.forwarder = forwarder;
    }
    
    public void setObjectName(final Object objectName) throws MalformedObjectNameException {
        this.objectName = ObjectNameManager.getInstance(objectName);
    }
    
    public void setThreaded(final boolean threaded) {
        this.threaded = threaded;
    }
    
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }
    
    @Override
    public void afterPropertiesSet() throws JMException, IOException {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }
        final JMXServiceURL url = new JMXServiceURL(this.serviceUrl);
        this.connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, this.environment, this.server);
        if (this.forwarder != null) {
            this.connectorServer.setMBeanServerForwarder(this.forwarder);
        }
        if (this.objectName != null) {
            this.doRegister(this.connectorServer, this.objectName);
        }
        try {
            if (this.threaded) {
                final Thread connectorThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            ConnectorServerFactoryBean.this.connectorServer.start();
                        }
                        catch (IOException ex) {
                            throw new JmxException("Could not start JMX connector server after delay", ex);
                        }
                    }
                };
                connectorThread.setName("JMX Connector Thread [" + this.serviceUrl + "]");
                connectorThread.setDaemon(this.daemon);
                connectorThread.start();
            }
            else {
                this.connectorServer.start();
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("JMX connector server started: " + this.connectorServer);
            }
        }
        catch (IOException ex) {
            this.unregisterBeans();
            throw ex;
        }
    }
    
    @Override
    public JMXConnectorServer getObject() {
        return this.connectorServer;
    }
    
    @Override
    public Class<? extends JMXConnectorServer> getObjectType() {
        return (this.connectorServer != null) ? this.connectorServer.getClass() : JMXConnectorServer.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() throws IOException {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Stopping JMX connector server: " + this.connectorServer);
        }
        try {
            this.connectorServer.stop();
        }
        finally {
            this.unregisterBeans();
        }
    }
}
