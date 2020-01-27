// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.jmx.support.WebSphereMBeanServerFactoryBean;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jmx.support.RegistrationPolicy;
import javax.management.MBeanServer;
import org.springframework.util.StringUtils;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;

@Configuration
public class MBeanExportConfiguration implements ImportAware, EnvironmentAware, BeanFactoryAware
{
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
    private AnnotationAttributes attributes;
    private Environment environment;
    private BeanFactory beanFactory;
    
    @Override
    public void setImportMetadata(final AnnotationMetadata importMetadata) {
        final Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        Assert.notNull(this.attributes = AnnotationAttributes.fromMap(map), "@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
    }
    
    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Bean(name = { "mbeanExporter" })
    @Role(2)
    public AnnotationMBeanExporter mbeanExporter() {
        final AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        this.setupDomain(exporter);
        this.setupServer(exporter);
        this.setupRegistrationPolicy(exporter);
        return exporter;
    }
    
    private void setupDomain(final AnnotationMBeanExporter exporter) {
        String defaultDomain = this.attributes.getString("defaultDomain");
        if (defaultDomain != null && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText(defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }
    
    private void setupServer(final AnnotationMBeanExporter exporter) {
        String server = this.attributes.getString("server");
        if (server != null && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText(server)) {
            exporter.setServer(this.beanFactory.getBean(server, MBeanServer.class));
        }
        else {
            final SpecificPlatform specificPlatform = SpecificPlatform.get();
            if (specificPlatform != null) {
                exporter.setServer(specificPlatform.getMBeanServer());
            }
        }
    }
    
    private void setupRegistrationPolicy(final AnnotationMBeanExporter exporter) {
        final RegistrationPolicy registrationPolicy = this.attributes.getEnum("registration");
        exporter.setRegistrationPolicy(registrationPolicy);
    }
    
    private enum SpecificPlatform
    {
        WEBLOGIC("weblogic.management.Helper") {
            public FactoryBean<?> getMBeanServerFactory() {
                final JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
                factory.setJndiName("java:comp/env/jmx/runtime");
                return factory;
            }
        }, 
        WEBSPHERE("com.ibm.websphere.management.AdminServiceFactory") {
            public FactoryBean<MBeanServer> getMBeanServerFactory() {
                return new WebSphereMBeanServerFactoryBean();
            }
        };
        
        private final String identifyingClass;
        
        private SpecificPlatform(final String identifyingClass) {
            this.identifyingClass = identifyingClass;
        }
        
        public MBeanServer getMBeanServer() {
            try {
                final Object server = this.getMBeanServerFactory().getObject();
                Assert.isInstanceOf(MBeanServer.class, server);
                return (MBeanServer)server;
            }
            catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        protected abstract FactoryBean<?> getMBeanServerFactory();
        
        public static SpecificPlatform get() {
            final ClassLoader classLoader = MBeanExportConfiguration.class.getClassLoader();
            for (final SpecificPlatform environment : values()) {
                if (ClassUtils.isPresent(environment.identifyingClass, classLoader)) {
                    return environment;
                }
            }
            return null;
        }
    }
}
