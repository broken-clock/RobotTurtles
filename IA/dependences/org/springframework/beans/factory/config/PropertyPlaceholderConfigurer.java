// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.PropertyPlaceholderHelper;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.util.StringValueResolver;
import java.util.Properties;
import org.springframework.core.SpringProperties;
import org.springframework.core.Constants;

public class PropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport
{
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
    private static final Constants constants;
    private int systemPropertiesMode;
    private boolean searchSystemEnvironment;
    
    public PropertyPlaceholderConfigurer() {
        this.systemPropertiesMode = 1;
        this.searchSystemEnvironment = !SpringProperties.getFlag("spring.getenv.ignore");
    }
    
    public void setSystemPropertiesModeName(final String constantName) throws IllegalArgumentException {
        this.systemPropertiesMode = PropertyPlaceholderConfigurer.constants.asNumber(constantName).intValue();
    }
    
    public void setSystemPropertiesMode(final int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }
    
    public void setSearchSystemEnvironment(final boolean searchSystemEnvironment) {
        this.searchSystemEnvironment = searchSystemEnvironment;
    }
    
    protected String resolvePlaceholder(final String placeholder, final Properties props, final int systemPropertiesMode) {
        String propVal = null;
        if (systemPropertiesMode == 2) {
            propVal = this.resolveSystemProperty(placeholder);
        }
        if (propVal == null) {
            propVal = this.resolvePlaceholder(placeholder, props);
        }
        if (propVal == null && systemPropertiesMode == 1) {
            propVal = this.resolveSystemProperty(placeholder);
        }
        return propVal;
    }
    
    protected String resolvePlaceholder(final String placeholder, final Properties props) {
        return props.getProperty(placeholder);
    }
    
    protected String resolveSystemProperty(final String key) {
        try {
            String value = System.getProperty(key);
            if (value == null && this.searchSystemEnvironment) {
                value = System.getenv(key);
            }
            return value;
        }
        catch (Throwable ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not access system property '" + key + "': " + ex);
            }
            return null;
        }
    }
    
    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess, final Properties props) throws BeansException {
        final StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }
    
    @Deprecated
    protected String parseStringValue(final String strVal, final Properties props, final Set<?> visitedPlaceholders) {
        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator, this.ignoreUnresolvablePlaceholders);
        final PropertyPlaceholderHelper.PlaceholderResolver resolver = new PropertyPlaceholderConfigurerResolver(props);
        return helper.replacePlaceholders(strVal, resolver);
    }
    
    static {
        constants = new Constants(PropertyPlaceholderConfigurer.class);
    }
    
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver
    {
        private final PropertyPlaceholderHelper helper;
        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;
        
        public PlaceholderResolvingStringValueResolver(final Properties props) {
            this.helper = new PropertyPlaceholderHelper(PropertyPlaceholderConfigurer.this.placeholderPrefix, PropertyPlaceholderConfigurer.this.placeholderSuffix, PropertyPlaceholderConfigurer.this.valueSeparator, PropertyPlaceholderConfigurer.this.ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
        }
        
        @Override
        public String resolveStringValue(final String strVal) throws BeansException {
            final String value = this.helper.replacePlaceholders(strVal, this.resolver);
            return value.equals(PropertyPlaceholderConfigurer.this.nullValue) ? null : value;
        }
    }
    
    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver
    {
        private final Properties props;
        
        private PropertyPlaceholderConfigurerResolver(final Properties props) {
            this.props = props;
        }
        
        @Override
        public String resolvePlaceholder(final String placeholderName) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, this.props, PropertyPlaceholderConfigurer.this.systemPropertiesMode);
        }
    }
}
