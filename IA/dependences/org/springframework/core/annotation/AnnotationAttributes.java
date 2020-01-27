// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.annotation;

import org.springframework.util.StringUtils;
import java.util.Iterator;
import java.lang.reflect.Array;
import org.springframework.util.Assert;
import java.util.Map;
import java.util.LinkedHashMap;

public class AnnotationAttributes extends LinkedHashMap<String, Object>
{
    public AnnotationAttributes() {
    }
    
    public AnnotationAttributes(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public AnnotationAttributes(final Map<String, Object> map) {
        super(map);
    }
    
    public String getString(final String attributeName) {
        return this.doGet(attributeName, String.class);
    }
    
    public String[] getStringArray(final String attributeName) {
        return this.doGet(attributeName, String[].class);
    }
    
    public boolean getBoolean(final String attributeName) {
        return this.doGet(attributeName, Boolean.class);
    }
    
    public <N extends Number> N getNumber(final String attributeName) {
        return this.doGet(attributeName, (Class<N>)Number.class);
    }
    
    public <E extends Enum<?>> E getEnum(final String attributeName) {
        return this.doGet(attributeName, (Class<E>)Enum.class);
    }
    
    public <T> Class<? extends T> getClass(final String attributeName) {
        return this.doGet(attributeName, (Class<Class<? extends T>>)Class.class);
    }
    
    public Class<?>[] getClassArray(final String attributeName) {
        return (Class<?>[])this.doGet(attributeName, (Class<Class[]>)Class[].class);
    }
    
    public AnnotationAttributes getAnnotation(final String attributeName) {
        return this.doGet(attributeName, AnnotationAttributes.class);
    }
    
    public AnnotationAttributes[] getAnnotationArray(final String attributeName) {
        return this.doGet(attributeName, AnnotationAttributes[].class);
    }
    
    private <T> T doGet(final String attributeName, final Class<T> expectedType) {
        Assert.hasText(attributeName, "attributeName must not be null or empty");
        Object value = ((LinkedHashMap<K, Object>)this).get(attributeName);
        Assert.notNull(value, String.format("Attribute '%s' not found", attributeName));
        if (!expectedType.isInstance(value)) {
            if (!expectedType.isArray() || !expectedType.getComponentType().isInstance(value)) {
                throw new IllegalArgumentException(String.format("Attribute '%s' is of type [%s], but [%s] was expected. Cause: ", attributeName, value.getClass().getSimpleName(), expectedType.getSimpleName()));
            }
            final Object arrayValue = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(arrayValue, 0, value);
            value = arrayValue;
        }
        return (T)value;
    }
    
    @Override
    public String toString() {
        final Iterator<Map.Entry<String, Object>> entries = this.entrySet().iterator();
        final StringBuilder sb = new StringBuilder("{");
        while (entries.hasNext()) {
            final Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(this.valueToString(entry.getValue()));
            sb.append(entries.hasNext() ? ", " : "");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String valueToString(final Object value) {
        if (value == this) {
            return "(this Map)";
        }
        if (value instanceof Object[]) {
            return "[" + StringUtils.arrayToCommaDelimitedString((Object[])value) + "]";
        }
        return String.valueOf(value);
    }
    
    public static AnnotationAttributes fromMap(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof AnnotationAttributes) {
            return (AnnotationAttributes)map;
        }
        return new AnnotationAttributes(map);
    }
}
