// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;
import java.io.IOException;
import org.springframework.jmx.MBeanServerNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.util.CollectionUtils;
import java.net.MalformedURLException;
import org.apache.commons.logging.LogFactory;
import javax.management.ObjectName;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServerConnection;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.support.NotificationListenerHolder;

public class NotificationListenerRegistrar extends NotificationListenerHolder implements InitializingBean, DisposableBean
{
    protected final Log logger;
    private MBeanServerConnection server;
    private JMXServiceURL serviceUrl;
    private Map<String, ?> environment;
    private String agentId;
    private final ConnectorDelegate connector;
    private ObjectName[] actualObjectNames;
    
    public NotificationListenerRegistrar() {
        this.logger = LogFactory.getLog(this.getClass());
        this.connector = new ConnectorDelegate();
    }
    
    public void setServer(final MBeanServerConnection server) {
        this.server = server;
    }
    
    public void setEnvironment(final Map<String, ?> environment) {
        this.environment = environment;
    }
    
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }
    
    public void setServiceUrl(final String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }
    
    public void setAgentId(final String agentId) {
        this.agentId = agentId;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.getNotificationListener() == null) {
            throw new IllegalArgumentException("Property 'notificationListener' is required");
        }
        if (CollectionUtils.isEmpty(this.mappedObjectNames)) {
            throw new IllegalArgumentException("Property 'mappedObjectName' is required");
        }
        this.prepare();
    }
    
    public void prepare() {
        if (this.server == null) {
            this.server = this.connector.connect(this.serviceUrl, this.environment, this.agentId);
        }
        try {
            this.actualObjectNames = this.getResolvedObjectNames();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Registering NotificationListener for MBeans " + Arrays.asList(this.actualObjectNames));
            }
            for (final ObjectName actualObjectName : this.actualObjectNames) {
                this.server.addNotificationListener(actualObjectName, this.getNotificationListener(), this.getNotificationFilter(), this.getHandback());
            }
        }
        catch (IOException ex) {
            throw new MBeanServerNotFoundException("Could not connect to remote MBeanServer at URL [" + this.serviceUrl + "]", ex);
        }
        catch (Exception ex2) {
            throw new JmxException("Unable to register NotificationListener", ex2);
        }
    }
    
    @Override
    public void destroy() {
        try {
            if (this.actualObjectNames != null) {
                for (final ObjectName actualObjectName : this.actualObjectNames) {
                    try {
                        this.server.removeNotificationListener(actualObjectName, this.getNotificationListener(), this.getNotificationFilter(), this.getHandback());
                    }
                    catch (Exception ex) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Unable to unregister NotificationListener", ex);
                        }
                    }
                }
            }
        }
        finally {
            this.connector.close();
        }
    }
}
