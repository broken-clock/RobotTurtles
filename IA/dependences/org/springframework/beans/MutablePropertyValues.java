// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.io.Serializable;

public class MutablePropertyValues implements PropertyValues, Serializable
{
    private final List<PropertyValue> propertyValueList;
    private Set<String> processedProperties;
    private volatile boolean converted;
    
    public MutablePropertyValues() {
        this.converted = false;
        this.propertyValueList = new ArrayList<PropertyValue>(0);
    }
    
    public MutablePropertyValues(final PropertyValues original) {
        this.converted = false;
        if (original != null) {
            final PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList<PropertyValue>(pvs.length);
            for (final PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        }
        else {
            this.propertyValueList = new ArrayList<PropertyValue>(0);
        }
    }
    
    public MutablePropertyValues(final Map<?, ?> original) {
        this.converted = false;
        if (original != null) {
            this.propertyValueList = new ArrayList<PropertyValue>(original.size());
            for (final Map.Entry<?, ?> entry : original.entrySet()) {
                this.propertyValueList.add(new PropertyValue(entry.getKey().toString(), entry.getValue()));
            }
        }
        else {
            this.propertyValueList = new ArrayList<PropertyValue>(0);
        }
    }
    
    public MutablePropertyValues(final List<PropertyValue> propertyValueList) {
        this.converted = false;
        this.propertyValueList = ((propertyValueList != null) ? propertyValueList : new ArrayList<PropertyValue>());
    }
    
    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }
    
    public int size() {
        return this.propertyValueList.size();
    }
    
    public MutablePropertyValues addPropertyValues(final PropertyValues other) {
        if (other != null) {
            final PropertyValue[] propertyValues;
            final PropertyValue[] pvs = propertyValues = other.getPropertyValues();
            for (final PropertyValue pv : propertyValues) {
                this.addPropertyValue(new PropertyValue(pv));
            }
        }
        return this;
    }
    
    public MutablePropertyValues addPropertyValues(final Map<?, ?> other) {
        if (other != null) {
            for (final Map.Entry<?, ?> entry : other.entrySet()) {
                this.addPropertyValue(new PropertyValue(entry.getKey().toString(), entry.getValue()));
            }
        }
        return this;
    }
    
    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); ++i) {
            final PropertyValue currentPv = this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                pv = this.mergeIfRequired(pv, currentPv);
                this.setPropertyValueAt(pv, i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }
    
    public void addPropertyValue(final String propertyName, final Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue));
    }
    
    public MutablePropertyValues add(final String propertyName, final Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue));
        return this;
    }
    
    public void setPropertyValueAt(final PropertyValue pv, final int i) {
        this.propertyValueList.set(i, pv);
    }
    
    private PropertyValue mergeIfRequired(final PropertyValue newPv, final PropertyValue currentPv) {
        final Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            final Mergeable mergeable = (Mergeable)value;
            if (mergeable.isMergeEnabled()) {
                final Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv.getName(), merged);
            }
        }
        return newPv;
    }
    
    public void removePropertyValue(final PropertyValue pv) {
        this.propertyValueList.remove(pv);
    }
    
    public void removePropertyValue(final String propertyName) {
        this.propertyValueList.remove(this.getPropertyValue(propertyName));
    }
    
    @Override
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[this.propertyValueList.size()]);
    }
    
    @Override
    public PropertyValue getPropertyValue(final String propertyName) {
        for (final PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }
    
    public Object get(final String propertyName) {
        final PropertyValue pv = this.getPropertyValue(propertyName);
        return (pv != null) ? pv.getValue() : null;
    }
    
    @Override
    public PropertyValues changesSince(final PropertyValues old) {
        final MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        }
        for (final PropertyValue newPv : this.propertyValueList) {
            final PropertyValue pvOld = old.getPropertyValue(newPv.getName());
            if (pvOld == null) {
                changes.addPropertyValue(newPv);
            }
            else {
                if (pvOld.equals(newPv)) {
                    continue;
                }
                changes.addPropertyValue(newPv);
            }
        }
        return changes;
    }
    
    @Override
    public boolean contains(final String propertyName) {
        return this.getPropertyValue(propertyName) != null || (this.processedProperties != null && this.processedProperties.contains(propertyName));
    }
    
    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }
    
    public void registerProcessedProperty(final String propertyName) {
        if (this.processedProperties == null) {
            this.processedProperties = new HashSet<String>();
        }
        this.processedProperties.add(propertyName);
    }
    
    public void setConverted() {
        this.converted = true;
    }
    
    public boolean isConverted() {
        return this.converted;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MutablePropertyValues)) {
            return false;
        }
        final MutablePropertyValues that = (MutablePropertyValues)other;
        return this.propertyValueList.equals(that.propertyValueList);
    }
    
    @Override
    public int hashCode() {
        return this.propertyValueList.hashCode();
    }
    
    @Override
    public String toString() {
        final PropertyValue[] pvs = this.getPropertyValues();
        final StringBuilder sb = new StringBuilder("PropertyValues: length=").append(pvs.length);
        if (pvs.length > 0) {
            sb.append("; ").append(StringUtils.arrayToDelimitedString(pvs, "; "));
        }
        return sb.toString();
    }
}
