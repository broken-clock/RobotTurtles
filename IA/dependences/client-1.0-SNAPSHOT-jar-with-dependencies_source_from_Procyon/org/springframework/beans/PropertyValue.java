// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.util.ObjectUtils;
import org.springframework.core.AttributeAccessor;
import org.springframework.util.Assert;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable
{
    private final String name;
    private final Object value;
    private Object source;
    private boolean optional;
    private boolean converted;
    private Object convertedValue;
    volatile Boolean conversionNecessary;
    volatile Object resolvedTokens;
    volatile PropertyDescriptor resolvedDescriptor;
    
    public PropertyValue(final String name, final Object value) {
        this.optional = false;
        this.converted = false;
        this.name = name;
        this.value = value;
    }
    
    public PropertyValue(final PropertyValue original) {
        this.optional = false;
        this.converted = false;
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
        this.source = original.getSource();
        this.optional = original.isOptional();
        this.converted = original.converted;
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.resolvedDescriptor = original.resolvedDescriptor;
        this.copyAttributesFrom(original);
    }
    
    public PropertyValue(final PropertyValue original, final Object newValue) {
        this.optional = false;
        this.converted = false;
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
        this.source = original;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.resolvedDescriptor = original.resolvedDescriptor;
        this.copyAttributesFrom(original);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original;
        for (original = this; original.source instanceof PropertyValue && original.source != original; original = (PropertyValue)original.source) {}
        return original;
    }
    
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    
    public synchronized boolean isConverted() {
        return this.converted;
    }
    
    public synchronized void setConvertedValue(final Object value) {
        this.converted = true;
        this.convertedValue = value;
    }
    
    public synchronized Object getConvertedValue() {
        return this.convertedValue;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PropertyValue)) {
            return false;
        }
        final PropertyValue otherPv = (PropertyValue)other;
        return this.name.equals(otherPv.name) && ObjectUtils.nullSafeEquals(this.value, otherPv.value) && ObjectUtils.nullSafeEquals(this.source, otherPv.source);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }
    
    @Override
    public String toString() {
        return "bean property '" + this.name + "'";
    }
}
