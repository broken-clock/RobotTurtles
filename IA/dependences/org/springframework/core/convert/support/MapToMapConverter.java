// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Iterator;
import java.util.List;
import org.springframework.core.CollectionFactory;
import java.util.ArrayList;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collections;
import java.util.Map;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class MapToMapConverter implements ConditionalGenericConverter
{
    private final ConversionService conversionService;
    
    public MapToMapConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Map.class, Map.class));
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.canConvertKey(sourceType, targetType) && this.canConvertValue(sourceType, targetType);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        boolean copyRequired = !targetType.getType().isInstance(source);
        final Map<Object, Object> sourceMap = (Map<Object, Object>)source;
        if (!copyRequired && sourceMap.isEmpty()) {
            return sourceMap;
        }
        final List<MapEntry> targetEntries = new ArrayList<MapEntry>(sourceMap.size());
        for (final Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
            final Object sourceKey = entry.getKey();
            final Object sourceValue = entry.getValue();
            final Object targetKey = this.convertKey(sourceKey, sourceType, targetType.getMapKeyTypeDescriptor());
            final Object targetValue = this.convertValue(sourceValue, sourceType, targetType.getMapValueTypeDescriptor());
            targetEntries.add(new MapEntry(targetKey, targetValue));
            if (sourceKey != targetKey || sourceValue != targetValue) {
                copyRequired = true;
            }
        }
        if (!copyRequired) {
            return sourceMap;
        }
        final Map<Object, Object> targetMap = CollectionFactory.createMap(targetType.getType(), sourceMap.size());
        for (final MapEntry entry2 : targetEntries) {
            entry2.addToMap(targetMap);
        }
        return targetMap;
    }
    
    private boolean canConvertKey(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapKeyTypeDescriptor(), targetType.getMapKeyTypeDescriptor(), this.conversionService);
    }
    
    private boolean canConvertValue(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapValueTypeDescriptor(), targetType.getMapValueTypeDescriptor(), this.conversionService);
    }
    
    private Object convertKey(final Object sourceKey, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceKey;
        }
        return this.conversionService.convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
    }
    
    private Object convertValue(final Object sourceValue, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceValue;
        }
        return this.conversionService.convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue), targetType);
    }
    
    private static class MapEntry
    {
        private Object key;
        private Object value;
        
        public MapEntry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public void addToMap(final Map<Object, Object> map) {
            map.put(this.key, this.value);
        }
    }
}
