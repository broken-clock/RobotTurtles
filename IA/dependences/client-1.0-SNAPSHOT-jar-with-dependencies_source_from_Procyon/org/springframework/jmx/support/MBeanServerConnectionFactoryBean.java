// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import javax.management.remote.JMXConnectorFactory;
import java.io.IOException;
import org.springframework.util.CollectionUtils;
import java.util.Properties;
import java.net.MalformedURLException;
import org.springframework.util.ClassUtils;
import java.util.HashMap;
import javax.management.remote.JMXConnector;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import javax.management.MBeanServerConnection;
import org.springframework.beans.factory.FactoryBean;

public class MBeanServerConnectionFactoryBean implements FactoryBean<MBeanServerConnection>, BeanClassLoaderAware, InitializingBean, DisposableBean
{
    private JMXServiceURL serviceUrl;
    private Map<String, Object> environment;
    private boolean connectOnStartup;
    private ClassLoader beanClassLoader;
    private JMXConnector connector;
    private MBeanServerConnection connection;
    private JMXConnectorLazyInitTargetSource connectorTargetSource;
    
    public MBeanServerConnectionFactoryBean() {
        this.environment = new HashMap<String, Object>();
        this.connectOnStartup = true;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setServiceUrl(final String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }
    
    public void setEnvironment(final Properties environment) {
        CollectionUtils.mergePropertiesIntoMap(environment, this.environment);
    }
    
    public void setEnvironmentMap(final Map<String, ?> environment) {
        if (environment != null) {
            this.environment.putAll(environment);
        }
    }
    
    public void setConnectOnStartup(final boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws IOException {
        if (this.serviceUrl == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
        if (this.connectOnStartup) {
            this.connect();
        }
        else {
            this.createLazyConnection();
        }
    }
    
    private void connect() throws IOException {
        this.connector = JMXConnectorFactory.connect(this.serviceUrl, this.environment);
        this.connection = this.connector.getMBeanServerConnection();
    }
    
    private void createLazyConnection() {
        this.connectorTargetSource = new JMXConnectorLazyInitTargetSource();
        final TargetSource connectionTargetSource = new MBeanServerConnectionLazyInitTargetSource();
        this.connector = (JMXConnector)new ProxyFactory(JMXConnector.class, this.connectorTargetSource).getProxy(this.beanClassLoader);
        this.connection = (MBeanServerConnection)new ProxyFactory(MBeanServerConnection.class, connectionTargetSource).getProxy(this.beanClassLoader);
    }
    
    @Override
    public MBeanServerConnection getObject() {
        return this.connection;
    }
    
    @Override
    public Class<? extends MBeanServerConnection> getObjectType() {
        return (this.connection != null) ? this.connection.getClass() : MBeanServerConnection.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() throws IOException {
        if (this.connectorTargetSource == null || this.connectorTargetSource.isInitialized()) {
            this.connector.close();
        }
    }
    
    private class JMXConnectorLazyInitTargetSource extends AbstractLazyCreationTargetSource
    {
        @Override
        protected Object createObject() throws Exception {
            return JMXConnectorFactory.connect(MBeanServerConnectionFactoryBean.this.serviceUrl, MBeanServerConnectionFactoryBean.this.environment);
        }
        
        @Override
        public Class<?> getTargetClass() {
            return JMXConnector.class;
        }
    }
    
    private class MBeanServerConnectionLazyInitTargetSource extends AbstractLazyCreationTargetSource
    {
        @Override
        protected Object createObject() throws Exception {
            return MBeanServerConnectionFactoryBean.this.connector.getMBeanServerConnection();
        }
        
        @Override
        public Class<?> getTargetClass() {
            return MBeanServerConnection.class;
        }
    }
}
