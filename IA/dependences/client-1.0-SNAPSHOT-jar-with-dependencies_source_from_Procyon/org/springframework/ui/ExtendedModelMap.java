// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ui;

import java.util.Map;
import java.util.Collection;

public class ExtendedModelMap extends ModelMap implements Model
{
    @Override
    public ExtendedModelMap addAttribute(final String attributeName, final Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
        return this;
    }
    
    @Override
    public ExtendedModelMap addAttribute(final Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }
    
    @Override
    public ExtendedModelMap addAllAttributes(final Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }
    
    @Override
    public ExtendedModelMap addAllAttributes(final Map<String, ?> attributes) {
        super.addAllAttributes(attributes);
        return this;
    }
    
    @Override
    public ExtendedModelMap mergeAttributes(final Map<String, ?> attributes) {
        super.mergeAttributes(attributes);
        return this;
    }
    
    @Override
    public Map<String, Object> asMap() {
        return this;
    }
}
