// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Map;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import java.util.Properties;

public class ManagedProperties extends Properties implements Mergeable, BeanMetadataElement
{
    private Object source;
    private boolean mergeEnabled;
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
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
        if (!(parent instanceof Properties)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        final Properties merged = new ManagedProperties();
        merged.putAll((Map<?, ?>)parent);
        merged.putAll(this);
        return merged;
    }
}
