// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class CompositePropertySource extends PropertySource<Object>
{
    private Set<PropertySource<?>> propertySources;
    
    public CompositePropertySource(final String name) {
        super(name);
        this.propertySources = new LinkedHashSet<PropertySource<?>>();
    }
    
    @Override
    public Object getProperty(final String name) {
        for (final PropertySource<?> propertySource : this.propertySources) {
            final Object candidate = propertySource.getProperty(name);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }
    
    public void addPropertySource(final PropertySource<?> propertySource) {
        this.propertySources.add(propertySource);
    }
    
    @Override
    public String toString() {
        return String.format("%s [name='%s', propertySources=%s]", this.getClass().getSimpleName(), this.name, this.propertySources);
    }
}
