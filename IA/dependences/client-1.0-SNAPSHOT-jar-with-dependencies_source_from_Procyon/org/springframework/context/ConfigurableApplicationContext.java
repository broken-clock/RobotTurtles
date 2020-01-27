// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import java.io.Closeable;

public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable
{
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
    public static final String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
    public static final String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";
    public static final String ENVIRONMENT_BEAN_NAME = "environment";
    public static final String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
    public static final String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
    
    void setId(final String p0);
    
    void setParent(final ApplicationContext p0);
    
    ConfigurableEnvironment getEnvironment();
    
    void setEnvironment(final ConfigurableEnvironment p0);
    
    void addBeanFactoryPostProcessor(final BeanFactoryPostProcessor p0);
    
    void addApplicationListener(final ApplicationListener<?> p0);
    
    void refresh() throws BeansException, IllegalStateException;
    
    void registerShutdownHook();
    
    void close();
    
    boolean isActive();
    
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
