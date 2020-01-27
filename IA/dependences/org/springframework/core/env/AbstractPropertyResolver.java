// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Iterator;
import java.util.LinkedHashSet;
import org.springframework.core.convert.support.DefaultConversionService;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.apache.commons.logging.Log;

public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver
{
    protected final Log logger;
    protected ConfigurableConversionService conversionService;
    private PropertyPlaceholderHelper nonStrictHelper;
    private PropertyPlaceholderHelper strictHelper;
    private boolean ignoreUnresolvableNestedPlaceholders;
    private String placeholderPrefix;
    private String placeholderSuffix;
    private String valueSeparator;
    private final Set<String> requiredProperties;
    
    public AbstractPropertyResolver() {
        this.logger = LogFactory.getLog(this.getClass());
        this.conversionService = new DefaultConversionService();
        this.ignoreUnresolvableNestedPlaceholders = false;
        this.placeholderPrefix = "${";
        this.placeholderSuffix = "}";
        this.valueSeparator = ":";
        this.requiredProperties = new LinkedHashSet<String>();
    }
    
    @Override
    public ConfigurableConversionService getConversionService() {
        return this.conversionService;
    }
    
    @Override
    public void setConversionService(final ConfigurableConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public String getProperty(final String key, final String defaultValue) {
        final String value = this.getProperty(key);
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public <T> T getProperty(final String key, final Class<T> targetType, final T defaultValue) {
        final T value = this.getProperty(key, targetType);
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public void setRequiredProperties(final String... requiredProperties) {
        for (final String key : requiredProperties) {
            this.requiredProperties.add(key);
        }
    }
    
    @Override
    public void validateRequiredProperties() {
        final MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
        for (final String key : this.requiredProperties) {
            if (this.getProperty(key) == null) {
                ex.addMissingRequiredProperty(key);
            }
        }
        if (!ex.getMissingRequiredProperties().isEmpty()) {
            throw ex;
        }
    }
    
    @Override
    public String getRequiredProperty(final String key) throws IllegalStateException {
        final String value = this.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(String.format("required key [%s] not found", key));
        }
        return value;
    }
    
    @Override
    public <T> T getRequiredProperty(final String key, final Class<T> valueType) throws IllegalStateException {
        final T value = this.getProperty(key, valueType);
        if (value == null) {
            throw new IllegalStateException(String.format("required key [%s] not found", key));
        }
        return value;
    }
    
    @Override
    public void setPlaceholderPrefix(final String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }
    
    @Override
    public void setPlaceholderSuffix(final String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }
    
    @Override
    public void setValueSeparator(final String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }
    
    @Override
    public String resolvePlaceholders(final String text) {
        if (this.nonStrictHelper == null) {
            this.nonStrictHelper = this.createPlaceholderHelper(true);
        }
        return this.doResolvePlaceholders(text, this.nonStrictHelper);
    }
    
    @Override
    public String resolveRequiredPlaceholders(final String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = this.createPlaceholderHelper(false);
        }
        return this.doResolvePlaceholders(text, this.strictHelper);
    }
    
    @Override
    public void setIgnoreUnresolvableNestedPlaceholders(final boolean ignoreUnresolvableNestedPlaceholders) {
        this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
    }
    
    protected String resolveNestedPlaceholders(final String value) {
        return this.ignoreUnresolvableNestedPlaceholders ? this.resolvePlaceholders(value) : this.resolveRequiredPlaceholders(value);
    }
    
    private PropertyPlaceholderHelper createPlaceholderHelper(final boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator, ignoreUnresolvablePlaceholders);
    }
    
    private String doResolvePlaceholders(final String text, final PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, new PropertyPlaceholderHelper.PlaceholderResolver() {
            @Override
            public String resolvePlaceholder(final String placeholderName) {
                return AbstractPropertyResolver.this.getPropertyAsRawString(placeholderName);
            }
        });
    }
    
    protected abstract String getPropertyAsRawString(final String p0);
}
