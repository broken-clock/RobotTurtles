// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import java.util.Iterator;
import java.util.Collection;
import javax.management.JMException;
import javax.management.ObjectInstance;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import org.apache.commons.logging.LogFactory;
import javax.management.ObjectName;
import java.util.Set;
import javax.management.MBeanServer;
import org.apache.commons.logging.Log;
import org.springframework.core.Constants;

public class MBeanRegistrationSupport
{
    @Deprecated
    public static final int REGISTRATION_FAIL_ON_EXISTING = 0;
    @Deprecated
    public static final int REGISTRATION_IGNORE_EXISTING = 1;
    @Deprecated
    public static final int REGISTRATION_REPLACE_EXISTING = 2;
    private static final Constants constants;
    protected final Log logger;
    protected MBeanServer server;
    private final Set<ObjectName> registeredBeans;
    private RegistrationPolicy registrationPolicy;
    
    public MBeanRegistrationSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.registeredBeans = new LinkedHashSet<ObjectName>();
        this.registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;
    }
    
    public void setServer(final MBeanServer server) {
        this.server = server;
    }
    
    public final MBeanServer getServer() {
        return this.server;
    }
    
    @Deprecated
    public void setRegistrationBehaviorName(final String registrationBehavior) {
        this.setRegistrationBehavior(MBeanRegistrationSupport.constants.asNumber(registrationBehavior).intValue());
    }
    
    @Deprecated
    public void setRegistrationBehavior(final int registrationBehavior) {
        this.setRegistrationPolicy(RegistrationPolicy.valueOf(registrationBehavior));
    }
    
    public void setRegistrationPolicy(final RegistrationPolicy registrationPolicy) {
        Assert.notNull(registrationPolicy, "RegistrationPolicy must not be null");
        this.registrationPolicy = registrationPolicy;
    }
    
    protected void doRegister(final Object mbean, final ObjectName objectName) throws JMException {
        ObjectName actualObjectName;
        synchronized (this.registeredBeans) {
            ObjectInstance registeredBean = null;
            Label_0211: {
                try {
                    registeredBean = this.server.registerMBean(mbean, objectName);
                }
                catch (InstanceAlreadyExistsException ex3) {
                    if (this.registrationPolicy != RegistrationPolicy.IGNORE_EXISTING) {
                        if (this.registrationPolicy == RegistrationPolicy.REPLACE_EXISTING) {
                            try {
                                if (this.logger.isDebugEnabled()) {
                                    this.logger.debug("Replacing existing MBean at [" + objectName + "]");
                                }
                                this.server.unregisterMBean(objectName);
                                registeredBean = this.server.registerMBean(mbean, objectName);
                                break Label_0211;
                            }
                            catch (InstanceNotFoundException ex2) {
                                this.logger.error("Unable to replace existing MBean at [" + objectName + "]", ex2);
                                throw ex3;
                            }
                        }
                        throw ex3;
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring existing MBean at [" + objectName + "]");
                    }
                }
            }
            actualObjectName = ((registeredBean != null) ? registeredBean.getObjectName() : null);
            if (actualObjectName == null) {
                actualObjectName = objectName;
            }
            this.registeredBeans.add(actualObjectName);
        }
        this.onRegister(actualObjectName, mbean);
    }
    
    protected void unregisterBeans() {
        final Set<ObjectName> snapshot;
        synchronized (this.registeredBeans) {
            snapshot = new LinkedHashSet<ObjectName>(this.registeredBeans);
        }
        for (final ObjectName objectName : snapshot) {
            this.doUnregister(objectName);
        }
    }
    
    protected void doUnregister(final ObjectName objectName) {
        boolean actuallyUnregistered = false;
        synchronized (this.registeredBeans) {
            if (this.registeredBeans.remove(objectName)) {
                try {
                    if (this.server.isRegistered(objectName)) {
                        this.server.unregisterMBean(objectName);
                        actuallyUnregistered = true;
                    }
                    else if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Could not unregister MBean [" + objectName + "] as said MBean " + "is not registered (perhaps already unregistered by an external process)");
                    }
                }
                catch (JMException ex) {
                    if (this.logger.isErrorEnabled()) {
                        this.logger.error("Could not unregister MBean [" + objectName + "]", ex);
                    }
                }
            }
        }
        if (actuallyUnregistered) {
            this.onUnregister(objectName);
        }
    }
    
    protected final ObjectName[] getRegisteredObjectNames() {
        synchronized (this.registeredBeans) {
            return this.registeredBeans.toArray(new ObjectName[this.registeredBeans.size()]);
        }
    }
    
    protected void onRegister(final ObjectName objectName, final Object mbean) {
        this.onRegister(objectName);
    }
    
    protected void onRegister(final ObjectName objectName) {
    }
    
    protected void onUnregister(final ObjectName objectName) {
    }
    
    static {
        constants = new Constants(MBeanRegistrationSupport.class);
    }
}
