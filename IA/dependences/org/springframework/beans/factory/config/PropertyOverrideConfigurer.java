// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyValue;
import java.util.Enumeration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import java.util.Properties;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

public class PropertyOverrideConfigurer extends PropertyResourceConfigurer
{
    public static final String DEFAULT_BEAN_NAME_SEPARATOR = ".";
    private String beanNameSeparator;
    private boolean ignoreInvalidKeys;
    private final Set<String> beanNames;
    
    public PropertyOverrideConfigurer() {
        this.beanNameSeparator = ".";
        this.ignoreInvalidKeys = false;
        this.beanNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));
    }
    
    public void setBeanNameSeparator(final String beanNameSeparator) {
        this.beanNameSeparator = beanNameSeparator;
    }
    
    public void setIgnoreInvalidKeys(final boolean ignoreInvalidKeys) {
        this.ignoreInvalidKeys = ignoreInvalidKeys;
    }
    
    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactory, final Properties props) throws BeansException {
        final Enumeration<?> names = props.propertyNames();
        while (names.hasMoreElements()) {
            final String key = (String)names.nextElement();
            try {
                this.processKey(beanFactory, key, props.getProperty(key));
            }
            catch (BeansException ex) {
                final String msg = "Could not process key '" + key + "' in PropertyOverrideConfigurer";
                if (!this.ignoreInvalidKeys) {
                    throw new BeanInitializationException(msg, ex);
                }
                if (!this.logger.isDebugEnabled()) {
                    continue;
                }
                this.logger.debug(msg, ex);
            }
        }
    }
    
    protected void processKey(final ConfigurableListableBeanFactory factory, final String key, final String value) throws BeansException {
        final int separatorIndex = key.indexOf(this.beanNameSeparator);
        if (separatorIndex == -1) {
            throw new BeanInitializationException("Invalid key '" + key + "': expected 'beanName" + this.beanNameSeparator + "property'");
        }
        final String beanName = key.substring(0, separatorIndex);
        final String beanProperty = key.substring(separatorIndex + 1);
        this.beanNames.add(beanName);
        this.applyPropertyValue(factory, beanName, beanProperty, value);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Property '" + key + "' set to value [" + value + "]");
        }
    }
    
    protected void applyPropertyValue(final ConfigurableListableBeanFactory factory, final String beanName, final String property, final String value) {
        BeanDefinition bd;
        for (bd = factory.getBeanDefinition(beanName); bd.getOriginatingBeanDefinition() != null; bd = bd.getOriginatingBeanDefinition()) {}
        final PropertyValue pv = new PropertyValue(property, value);
        pv.setOptional(this.ignoreInvalidKeys);
        bd.getPropertyValues().addPropertyValue(pv);
    }
    
    public boolean hasPropertyOverridesFor(final String beanName) {
        return this.beanNames.contains(beanName);
    }
}
