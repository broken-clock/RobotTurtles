// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Map;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import java.util.LinkedHashMap;

public class ManagedMap<K, V> extends LinkedHashMap<K, V> implements Mergeable, BeanMetadataElement
{
    private Object source;
    private String keyTypeName;
    private String valueTypeName;
    private boolean mergeEnabled;
    
    public ManagedMap() {
    }
    
    public ManagedMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public void setKeyTypeName(final String keyTypeName) {
        this.keyTypeName = keyTypeName;
    }
    
    public String getKeyTypeName() {
        return this.keyTypeName;
    }
    
    public void setValueTypeName(final String valueTypeName) {
        this.valueTypeName = valueTypeName;
    }
    
    public String getValueTypeName() {
        return this.valueTypeName;
    }
    
    public void setMergeEnabled(final boolean mergeEnabled) {
        this.mergeEnabled = mergeEnabled;
    }
    
    @Override
    public boolean isMergeEnabled() {
        return this.mergeEnabled;
    }
    
    @Override
    public Object merge(final Object parent) {
        if (!this.mergeEnabled) {
            throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
        }
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof Map)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        final Map<K, V> merged = new ManagedMap();
        merged.putAll((Map<? extends K, ? extends V>)parent);
        merged.putAll((Map<? extends K, ? extends V>)this);
        return merged;
    }
}
