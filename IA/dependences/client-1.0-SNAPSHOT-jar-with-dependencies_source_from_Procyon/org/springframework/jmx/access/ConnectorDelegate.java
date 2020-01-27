// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.support.JmxUtils;
import java.io.IOException;
import org.springframework.jmx.MBeanServerNotFoundException;
import javax.management.remote.JMXConnectorFactory;
import javax.management.MBeanServerConnection;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnector;
import org.apache.commons.logging.Log;

class ConnectorDelegate
{
    private static final Log logger;
    private JMXConnector connector;
    
    public MBeanServerConnection connect(final JMXServiceURL serviceUrl, final Map<String, ?> environment, final String agentId) throws MBeanServerNotFoundException {
        if (serviceUrl != null) {
            if (ConnectorDelegate.logger.isDebugEnabled()) {
                ConnectorDelegate.logger.debug("Connecting to remote MBeanServer at URL [" + serviceUrl + "]");
            }
            try {
                this.connector = JMXConnectorFactory.connect(serviceUrl, environment);
                return this.connector.getMBeanServerConnection();
            }
            catch (IOException ex) {
                throw new MBeanServerNotFoundException("Could not connect to remote MBeanServer [" + serviceUrl + "]", ex);
            }
        }
        ConnectorDelegate.logger.debug("Attempting to locate local MBeanServer");
        return JmxUtils.locateMBeanServer(agentId);
    }
    
    public void close() {
        if (this.connector != null) {
            try {
                this.connector.close();
            }
            catch (IOException ex) {
                ConnectorDelegate.logger.debug("Could not close JMX connector", ex);
            }
        }
    }
    
    static {
        logger = LogFactory.getLog(ConnectorDelegate.class);
    }
}
