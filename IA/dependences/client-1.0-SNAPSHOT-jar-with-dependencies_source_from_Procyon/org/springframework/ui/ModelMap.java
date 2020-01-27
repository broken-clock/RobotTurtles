// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ui;

import java.util.Map;
import java.util.Iterator;
import org.springframework.core.Conventions;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.LinkedHashMap;

public class ModelMap extends LinkedHashMap<String, Object>
{
    public ModelMap() {
    }
    
    public ModelMap(final String attributeName, final Object attributeValue) {
        this.addAttribute(attributeName, attributeValue);
    }
    
    public ModelMap(final Object attributeValue) {
        this.addAttribute(attributeValue);
    }
    
    public ModelMap addAttribute(final String attributeName, final Object attributeValue) {
        Assert.notNull(attributeName, "Model attribute name must not be null");
        this.put(attributeName, attributeValue);
        return this;
    }
    
    public ModelMap addAttribute(final Object attributeValue) {
        Assert.notNull(attributeValue, "Model object must not be null");
        if (attributeValue instanceof Collection && ((Collection)attributeValue).isEmpty()) {
            return this;
        }
        return this.addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
    }
    
    public ModelMap addAllAttributes(final Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (final Object attributeValue : attributeValues) {
                this.addAttribute(attributeValue);
            }
        }
        return this;
    }
    
    public ModelMap addAllAttributes(final Map<String, ?> attributes) {
        if (attributes != null) {
            this.putAll(attributes);
        }
        return this;
    }
    
    public ModelMap mergeAttributes(final Map<String, ?> attributes) {
        if (attributes != null) {
            for (final String key : attributes.keySet()) {
                if (!this.containsKey(key)) {
                    this.put(key, attributes.get(key));
                }
            }
        }
        return this;
    }
    
    public boolean containsAttribute(final String attributeName) {
        return this.containsKey(attributeName);
    }
}
