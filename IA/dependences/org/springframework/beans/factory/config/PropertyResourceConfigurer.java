// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.Enumeration;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.BeansException;
import java.util.Properties;
import java.io.IOException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.support.PropertiesLoaderSupport;

public abstract class PropertyResourceConfigurer extends PropertiesLoaderSupport implements BeanFactoryPostProcessor, PriorityOrdered
{
    private int order;
    
    public PropertyResourceConfigurer() {
        this.order = Integer.MAX_VALUE;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            final Properties mergedProps = this.mergeProperties();
            this.convertProperties(mergedProps);
            this.processProperties(beanFactory, mergedProps);
        }
        catch (IOException ex) {
            throw new BeanInitializationException("Could not load properties", ex);
        }
    }
    
    protected void convertProperties(final Properties props) {
        final Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String propertyName = (String)propertyNames.nextElement();
            final String propertyValue = props.getProperty(propertyName);
            final String convertedValue = this.convertProperty(propertyName, propertyValue);
            if (!ObjectUtils.nullSafeEquals(propertyValue, convertedValue)) {
                props.setProperty(propertyName, convertedValue);
            }
        }
    }
    
    protected String convertProperty(final String propertyName, final String propertyValue) {
        return this.convertPropertyValue(propertyValue);
    }
    
    protected String convertPropertyValue(final String originalValue) {
        return originalValue;
    }
    
    protected abstract void processProperties(final ConfigurableListableBeanFactory p0, final Properties p1) throws BeansException;
}
