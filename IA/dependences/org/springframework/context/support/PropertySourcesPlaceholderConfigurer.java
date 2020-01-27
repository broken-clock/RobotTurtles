// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.Assert;
import java.util.Properties;
import org.springframework.util.StringValueResolver;
import org.springframework.beans.BeansException;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import java.io.IOException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

public class PropertySourcesPlaceholderConfigurer extends PlaceholderConfigurerSupport implements EnvironmentAware
{
    public static final String LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME = "localProperties";
    public static final String ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME = "environmentProperties";
    private MutablePropertySources propertySources;
    private PropertySources appliedPropertySources;
    private Environment environment;
    
    public void setPropertySources(final PropertySources propertySources) {
        this.propertySources = new MutablePropertySources(propertySources);
    }
    
    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = new MutablePropertySources();
            if (this.environment != null) {
                this.propertySources.addLast(new PropertySource<Environment>("environmentProperties", this.environment) {
                    @Override
                    public String getProperty(final String key) {
                        return ((Environment)this.source).getProperty(key);
                    }
                });
            }
            try {
                final PropertySource<?> localPropertySource = new PropertiesPropertySource("localProperties", this.mergeProperties());
                if (this.localOverride) {
                    this.propertySources.addFirst(localPropertySource);
                }
                else {
                    this.propertySources.addLast(localPropertySource);
                }
            }
            catch (IOException ex) {
                throw new BeanInitializationException("Could not load properties", ex);
            }
        }
        this.processProperties(beanFactory, new PropertySourcesPropertyResolver(this.propertySources));
        this.appliedPropertySources = this.propertySources;
    }
    
    protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess, final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);
        final StringValueResolver valueResolver = new StringValueResolver() {
            @Override
            public String resolveStringValue(final String strVal) {
                final String resolved = PropertySourcesPlaceholderConfigurer.this.ignoreUnresolvablePlaceholders ? propertyResolver.resolvePlaceholders(strVal) : propertyResolver.resolveRequiredPlaceholders(strVal);
                return resolved.equals(PropertySourcesPlaceholderConfigurer.this.nullValue) ? null : resolved;
            }
        };
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }
    
    @Deprecated
    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactory, final Properties props) {
        throw new UnsupportedOperationException("Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }
    
    public PropertySources getAppliedPropertySources() throws IllegalStateException {
        Assert.state(this.appliedPropertySources != null, "PropertySources have not get been applied");
        return this.appliedPropertySources;
    }
}
