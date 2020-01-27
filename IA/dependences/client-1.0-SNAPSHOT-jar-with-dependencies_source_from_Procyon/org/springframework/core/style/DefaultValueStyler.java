// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.style;

import java.util.Set;
import java.util.List;
import java.util.Iterator;
import org.springframework.util.ObjectUtils;
import java.util.Collection;
import java.util.Map;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;

public class DefaultValueStyler implements ValueStyler
{
    private static final String EMPTY = "[empty]";
    private static final String NULL = "[null]";
    private static final String COLLECTION = "collection";
    private static final String SET = "set";
    private static final String LIST = "list";
    private static final String MAP = "map";
    private static final String ARRAY = "array";
    
    @Override
    public String style(final Object value) {
        if (value == null) {
            return "[null]";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Class) {
            return ClassUtils.getShortName((Class<?>)value);
        }
        if (value instanceof Method) {
            final Method method = (Method)value;
            return method.getName() + "@" + ClassUtils.getShortName(method.getDeclaringClass());
        }
        if (value instanceof Map) {
            return this.style((Map<Object, Object>)value);
        }
        if (value instanceof Map.Entry) {
            return this.style((Map.Entry<?, ?>)value);
        }
        if (value instanceof Collection) {
            return this.style((Collection<?>)value);
        }
        if (value.getClass().isArray()) {
            return this.styleArray(ObjectUtils.toObjectArray(value));
        }
        return String.valueOf(value);
    }
    
    private <K, V> String style(final Map<K, V> value) {
        final StringBuilder result = new StringBuilder(value.size() * 8 + 16);
        result.append("map[");
        final Iterator<Map.Entry<K, V>> it = value.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<K, V> entry = it.next();
            result.append(this.style(entry));
            if (it.hasNext()) {
                result.append(',').append(' ');
            }
        }
        if (value.isEmpty()) {
            result.append("[empty]");
        }
        result.append("]");
        return result.toString();
    }
    
    private String style(final Map.Entry<?, ?> value) {
        return this.style(value.getKey()) + " -> " + this.style(value.getValue());
    }
    
    private String style(final Collection<?> value) {
        final StringBuilder result = new StringBuilder(value.size() * 8 + 16);
        result.append(this.getCollectionTypeString(value)).append('[');
        final Iterator<?> i = value.iterator();
        while (i.hasNext()) {
            result.append(this.style(i.next()));
            if (i.hasNext()) {
                result.append(',').append(' ');
            }
        }
        if (value.isEmpty()) {
            result.append("[empty]");
        }
        result.append("]");
        return result.toString();
    }
    
    private String getCollectionTypeString(final Collection<?> value) {
        if (value instanceof List) {
            return "list";
        }
        if (value instanceof Set) {
            return "set";
        }
        return "collection";
    }
    
    private String styleArray(final Object[] array) {
        final StringBuilder result = new StringBuilder(array.length * 8 + 16);
        result.append("array<").append(ClassUtils.getShortName(array.getClass().getComponentType())).append(">[");
        for (int i = 0; i < array.length - 1; ++i) {
            result.append(this.style(array[i]));
            result.append(',').append(' ');
        }
        if (array.length > 0) {
            result.append(this.style(array[array.length - 1]));
        }
        else {
            result.append("[empty]");
        }
        result.append("]");
        return result.toString();
    }
}
