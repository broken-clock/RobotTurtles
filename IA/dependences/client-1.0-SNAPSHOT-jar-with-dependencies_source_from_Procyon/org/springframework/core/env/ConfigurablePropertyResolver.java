// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;

public interface ConfigurablePropertyResolver extends PropertyResolver
{
    ConfigurableConversionService getConversionService();
    
    void setConversionService(final ConfigurableConversionService p0);
    
    void setPlaceholderPrefix(final String p0);
    
    void setPlaceholderSuffix(final String p0);
    
    void setValueSeparator(final String p0);
    
    void setRequiredProperties(final String... p0);
    
    void validateRequiredProperties() throws MissingRequiredPropertiesException;
    
    void setIgnoreUnresolvableNestedPlaceholders(final boolean p0);
}
