// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.Map;
import java.beans.PropertyEditorSupport;

public class CustomMapEditor extends PropertyEditorSupport
{
    private final Class<? extends Map> mapType;
    private final boolean nullAsEmptyMap;
    
    public CustomMapEditor(final Class<? extends Map> mapType) {
        this(mapType, false);
    }
    
    public CustomMapEditor(final Class<? extends Map> mapType, final boolean nullAsEmptyMap) {
        if (mapType == null) {
            throw new IllegalArgumentException("Map type is required");
        }
        if (!Map.class.isAssignableFrom(mapType)) {
            throw new IllegalArgumentException("Map type [" + mapType.getName() + "] does not implement [java.util.Map]");
        }
        this.mapType = mapType;
        this.nullAsEmptyMap = nullAsEmptyMap;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(text);
    }
    
    @Override
    public void setValue(final Object value) {
        if (value == null && this.nullAsEmptyMap) {
            super.setValue(this.createMap(this.mapType, 0));
        }
        else if (value == null || (this.mapType.isInstance(value) && !this.alwaysCreateNewMap())) {
            super.setValue(value);
        }
        else {
            if (!(value instanceof Map)) {
                throw new IllegalArgumentException("Value cannot be converted to Map: " + value);
            }
            final Map<?, ?> source = (Map<?, ?>)value;
            final Map<Object, Object> target = this.createMap(this.mapType, source.size());
            for (final Map.Entry<?, ?> entry : source.entrySet()) {
                target.put(this.convertKey(entry.getKey()), this.convertValue(entry.getValue()));
            }
            super.setValue(target);
        }
    }
    
    protected Map<Object, Object> createMap(final Class<? extends Map> mapType, final int initialCapacity) {
        if (!mapType.isInterface()) {
            try {
                return (Map<Object, Object>)mapType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate map class [" + mapType.getName() + "]: " + ex.getMessage());
            }
        }
        if (SortedMap.class.equals(mapType)) {
            return new TreeMap<Object, Object>();
        }
        return new LinkedHashMap<Object, Object>(initialCapacity);
    }
    
    protected boolean alwaysCreateNewMap() {
        return false;
    }
    
    protected Object convertKey(final Object key) {
        return key;
    }
    
    protected Object convertValue(final Object value) {
        return value;
    }
    
    @Override
    public String getAsText() {
        return null;
    }
}
