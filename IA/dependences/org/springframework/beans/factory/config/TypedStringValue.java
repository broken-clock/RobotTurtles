// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.ObjectUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.beans.BeanMetadataElement;

public class TypedStringValue implements BeanMetadataElement
{
    private String value;
    private volatile Object targetType;
    private Object source;
    private String specifiedTypeName;
    private volatile boolean dynamic;
    
    public TypedStringValue(final String value) {
        this.setValue(value);
    }
    
    public TypedStringValue(final String value, final Class<?> targetType) {
        this.setValue(value);
        this.setTargetType(targetType);
    }
    
    public TypedStringValue(final String value, final String targetTypeName) {
        this.setValue(value);
        this.setTargetTypeName(targetTypeName);
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setTargetType(final Class<?> targetType) {
        Assert.notNull(targetType, "'targetType' must not be null");
        this.targetType = targetType;
    }
    
    public Class<?> getTargetType() {
        final Object targetTypeValue = this.targetType;
        if (!(targetTypeValue instanceof Class)) {
            throw new IllegalStateException("Typed String value does not carry a resolved target type");
        }
        return (Class<?>)targetTypeValue;
    }
    
    public void setTargetTypeName(final String targetTypeName) {
        Assert.notNull(targetTypeName, "'targetTypeName' must not be null");
        this.targetType = targetTypeName;
    }
    
    public String getTargetTypeName() {
        final Object targetTypeValue = this.targetType;
        if (targetTypeValue instanceof Class) {
            return ((Class)targetTypeValue).getName();
        }
        return (String)targetTypeValue;
    }
    
    public boolean hasTargetType() {
        return this.targetType instanceof Class;
    }
    
    public Class<?> resolveTargetType(final ClassLoader classLoader) throws ClassNotFoundException {
        if (this.targetType == null) {
            return null;
        }
        final Class<?> resolvedClass = ClassUtils.forName(this.getTargetTypeName(), classLoader);
        return (Class<?>)(this.targetType = resolvedClass);
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public void setSpecifiedTypeName(final String specifiedTypeName) {
        this.specifiedTypeName = specifiedTypeName;
    }
    
    public String getSpecifiedTypeName() {
        return this.specifiedTypeName;
    }
    
    public void setDynamic() {
        this.dynamic = true;
    }
    
    public boolean isDynamic() {
        return this.dynamic;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypedStringValue)) {
            return false;
        }
        final TypedStringValue otherValue = (TypedStringValue)other;
        return ObjectUtils.nullSafeEquals(this.value, otherValue.value) && ObjectUtils.nullSafeEquals(this.targetType, otherValue.targetType);
    }
    
    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.targetType);
    }
    
    @Override
    public String toString() {
        return "TypedStringValue: value [" + this.value + "], target type [" + this.targetType + "]";
    }
}
