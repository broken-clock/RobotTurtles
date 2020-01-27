// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import javax.management.MBeanServerFactory;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import javax.management.MBeanServer;
import org.springframework.beans.factory.FactoryBean;

public class MBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean, DisposableBean
{
    protected final Log logger;
    private boolean locateExistingServerIfPossible;
    private String agentId;
    private String defaultDomain;
    private boolean registerWithFactory;
    private MBeanServer server;
    private boolean newlyRegistered;
    
    public MBeanServerFactoryBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.locateExistingServerIfPossible = false;
        this.registerWithFactory = true;
        this.newlyRegistered = false;
    }
    
    public void setLocateExistingServerIfPossible(final boolean locateExistingServerIfPossible) {
        this.locateExistingServerIfPossible = locateExistingServerIfPossible;
    }
    
    public void setAgentId(final String agentId) {
        this.agentId = agentId;
    }
    
    public void setDefaultDomain(final String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }
    
    public void setRegisterWithFactory(final boolean registerWithFactory) {
        this.registerWithFactory = registerWithFactory;
    }
    
    @Override
    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        Label_0050: {
            if (!this.locateExistingServerIfPossible) {
                if (this.agentId == null) {
                    break Label_0050;
                }
            }
            try {
                this.server = this.locateMBeanServer(this.agentId);
            }
            catch (MBeanServerNotFoundException ex) {
                if (this.agentId != null) {
                    throw ex;
                }
                this.logger.info("No existing MBeanServer found - creating new one");
            }
        }
        if (this.server == null) {
            this.server = this.createMBeanServer(this.defaultDomain, this.registerWithFactory);
            this.newlyRegistered = this.registerWithFactory;
        }
    }
    
    protected MBeanServer locateMBeanServer(final String agentId) throws MBeanServerNotFoundException {
        return JmxUtils.locateMBeanServer(agentId);
    }
    
    protected MBeanServer createMBeanServer(final String defaultDomain, final boolean registerWithFactory) {
        if (registerWithFactory) {
            return MBeanServerFactory.createMBeanServer(defaultDomain);
        }
        return MBeanServerFactory.newMBeanServer(defaultDomain);
    }
    
    @Override
    public MBeanServer getObject() {
        return this.server;
    }
    
    @Override
    public Class<? extends MBeanServer> getObjectType() {
        return (this.server != null) ? this.server.getClass() : MBeanServer.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() {
        if (this.newlyRegistered) {
            MBeanServerFactory.releaseMBeanServer(this.server);
        }
    }
}
