// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Collection;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import java.util.LinkedHashSet;

public class ManagedSet<E> extends LinkedHashSet<E> implements Mergeable, BeanMetadataElement
{
    private Object source;
    private String elementTypeName;
    private boolean mergeEnabled;
    
    public ManagedSet() {
    }
    
    public ManagedSet(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public void setElementTypeName(final String elementTypeName) {
        this.elementTypeName = elementTypeName;
    }
    
    public String getElementTypeName() {
        return this.elementTypeName;
    }
    
    public void setMergeEnabled(final boolean mergeEnabled) {
        this.mergeEnabled = mergeEnabled;
    }
    
    @Override
    public boolean isMergeEnabled() {
        return this.mergeEnabled;
    }
    
    @Override
    public Set<E> merge(final Object parent) {
        if (!this.mergeEnabled) {
            throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
        }
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof Set)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        final Set<E> merged = new ManagedSet();
        merged.addAll((Collection<? extends E>)parent);
        merged.addAll((Collection<? extends E>)this);
        return merged;
    }
}
