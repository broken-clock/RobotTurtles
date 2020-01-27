// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import org.springframework.util.Assert;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;

public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable
{
    private final Map<String, Object> attributes;
    
    public AttributeAccessorSupport() {
        this.attributes = new LinkedHashMap<String, Object>(0);
    }
    
    @Override
    public void setAttribute(final String name, final Object value) {
        Assert.notNull(name, "Name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            this.removeAttribute(name);
        }
    }
    
    @Override
    public Object getAttribute(final String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.get(name);
    }
    
    @Override
    public Object removeAttribute(final String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.remove(name);
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.containsKey(name);
    }
    
    @Override
    public String[] attributeNames() {
        return this.attributes.keySet().toArray(new String[this.attributes.size()]);
    }
    
    protected void copyAttributesFrom(final AttributeAccessor source) {
        Assert.notNull(source, "Source must not be null");
        final String[] attributeNames2;
        final String[] attributeNames = attributeNames2 = source.attributeNames();
        for (final String attributeName : attributeNames2) {
            this.setAttribute(attributeName, source.getAttribute(attributeName));
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AttributeAccessorSupport)) {
            return false;
        }
        final AttributeAccessorSupport that = (AttributeAccessorSupport)other;
        return this.attributes.equals(that.attributes);
    }
    
    @Override
    public int hashCode() {
        return this.attributes.hashCode();
    }
}
