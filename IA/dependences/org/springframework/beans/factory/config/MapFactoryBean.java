// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.Iterator;
import org.springframework.beans.TypeConverter;
import org.springframework.core.GenericCollectionTypeResolver;
import java.util.LinkedHashMap;
import org.springframework.beans.BeanUtils;
import java.util.Map;

public class MapFactoryBean extends AbstractFactoryBean<Map<Object, Object>>
{
    private Map<?, ?> sourceMap;
    private Class<? extends Map> targetMapClass;
    
    public void setSourceMap(final Map<?, ?> sourceMap) {
        this.sourceMap = sourceMap;
    }
    
    public void setTargetMapClass(final Class<? extends Map> targetMapClass) {
        if (targetMapClass == null) {
            throw new IllegalArgumentException("'targetMapClass' must not be null");
        }
        if (!Map.class.isAssignableFrom(targetMapClass)) {
            throw new IllegalArgumentException("'targetMapClass' must implement [java.util.Map]");
        }
        this.targetMapClass = targetMapClass;
    }
    
    @Override
    public Class<Map> getObjectType() {
        return Map.class;
    }
    
    @Override
    protected Map<Object, Object> createInstance() {
        if (this.sourceMap == null) {
            throw new IllegalArgumentException("'sourceMap' is required");
        }
        Map<Object, Object> result = null;
        if (this.targetMapClass != null) {
            result = BeanUtils.instantiateClass(this.targetMapClass);
        }
        else {
            result = new LinkedHashMap<Object, Object>(this.sourceMap.size());
        }
        Class<?> keyType = null;
        Class<?> valueType = null;
        if (this.targetMapClass != null) {
            keyType = GenericCollectionTypeResolver.getMapKeyType(this.targetMapClass);
            valueType = GenericCollectionTypeResolver.getMapValueType(this.targetMapClass);
        }
        if (keyType != null || valueType != null) {
            final TypeConverter converter = this.getBeanTypeConverter();
            for (final Map.Entry<?, ?> entry : this.sourceMap.entrySet()) {
                final Object convertedKey = converter.convertIfNecessary(entry.getKey(), keyType);
                final Object convertedValue = converter.convertIfNecessary(entry.getValue(), valueType);
                result.put(convertedKey, convertedValue);
            }
        }
        else {
            result.putAll(this.sourceMap);
        }
        return result;
    }
}
