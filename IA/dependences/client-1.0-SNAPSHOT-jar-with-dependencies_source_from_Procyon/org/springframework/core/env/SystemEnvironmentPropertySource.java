// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.util.Assert;
import java.util.Map;

public class SystemEnvironmentPropertySource extends MapPropertySource
{
    public SystemEnvironmentPropertySource(final String name, final Map<String, Object> source) {
        super(name, source);
    }
    
    @Override
    public boolean containsProperty(final String name) {
        return this.getProperty(name) != null;
    }
    
    @Override
    public Object getProperty(final String name) {
        Assert.notNull(name, "property name must not be null");
        final String actualName = this.resolvePropertyName(name);
        if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
            this.logger.debug(String.format("PropertySource [%s] does not contain '%s', but found equivalent '%s'", this.getName(), name, actualName));
        }
        return super.getProperty(actualName);
    }
    
    private String resolvePropertyName(final String name) {
        if (super.containsProperty(name)) {
            return name;
        }
        final String usName = name.replace('.', '_');
        if (!name.equals(usName) && super.containsProperty(usName)) {
            return usName;
        }
        final String ucName = name.toUpperCase();
        if (!name.equals(ucName)) {
            if (super.containsProperty(ucName)) {
                return ucName;
            }
            final String usUcName = ucName.replace('.', '_');
            if (!ucName.equals(usUcName) && super.containsProperty(usUcName)) {
                return usUcName;
            }
        }
        return name;
    }
}
