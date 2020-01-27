// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.beans.factory.InitializingBean;
import javax.management.MBeanServer;
import org.springframework.beans.factory.FactoryBean;

public class WebSphereMBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean
{
    private static final String ADMIN_SERVICE_FACTORY_CLASS = "com.ibm.websphere.management.AdminServiceFactory";
    private static final String GET_MBEAN_FACTORY_METHOD = "getMBeanFactory";
    private static final String GET_MBEAN_SERVER_METHOD = "getMBeanServer";
    private MBeanServer mbeanServer;
    
    @Override
    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        try {
            final Class<?> adminServiceClass = this.getClass().getClassLoader().loadClass("com.ibm.websphere.management.AdminServiceFactory");
            final Method getMBeanFactoryMethod = adminServiceClass.getMethod("getMBeanFactory", (Class<?>[])new Class[0]);
            final Object mbeanFactory = getMBeanFactoryMethod.invoke(null, new Object[0]);
            final Method getMBeanServerMethod = mbeanFactory.getClass().getMethod("getMBeanServer", (Class<?>[])new Class[0]);
            this.mbeanServer = (MBeanServer)getMBeanServerMethod.invoke(mbeanFactory, new Object[0]);
        }
        catch (ClassNotFoundException ex) {
            throw new MBeanServerNotFoundException("Could not find WebSphere's AdminServiceFactory class", ex);
        }
        catch (InvocationTargetException ex2) {
            throw new MBeanServerNotFoundException("WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method failed", ex2.getTargetException());
        }
        catch (Exception ex3) {
            throw new MBeanServerNotFoundException("Could not access WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method", ex3);
        }
    }
    
    @Override
    public MBeanServer getObject() {
        return this.mbeanServer;
    }
    
    @Override
    public Class<? extends MBeanServer> getObjectType() {
        return (this.mbeanServer != null) ? this.mbeanServer.getClass() : MBeanServer.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
