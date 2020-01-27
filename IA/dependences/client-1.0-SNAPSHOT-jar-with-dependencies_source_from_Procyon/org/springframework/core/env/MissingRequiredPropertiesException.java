// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.LinkedHashSet;
import java.util.Set;

public class MissingRequiredPropertiesException extends IllegalStateException
{
    private final Set<String> missingRequiredProperties;
    
    public MissingRequiredPropertiesException() {
        this.missingRequiredProperties = new LinkedHashSet<String>();
    }
    
    public Set<String> getMissingRequiredProperties() {
        return this.missingRequiredProperties;
    }
    
    void addMissingRequiredProperty(final String key) {
        this.missingRequiredProperties.add(key);
    }
    
    @Override
    public String getMessage() {
        return String.format("The following properties were declared as required but could not be resolved: %s", this.getMissingRequiredProperties());
    }
}
