// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.ObjectUtils;
import org.springframework.beans.BeanMetadataElement;
import java.util.Set;
import java.util.Collections;
import org.springframework.util.ClassUtils;
import org.springframework.beans.Mergeable;
import org.springframework.util.Assert;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConstructorArgumentValues
{
    private final Map<Integer, ValueHolder> indexedArgumentValues;
    private final List<ValueHolder> genericArgumentValues;
    
    public ConstructorArgumentValues() {
        this.indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>(0);
        this.genericArgumentValues = new LinkedList<ValueHolder>();
    }
    
    public ConstructorArgumentValues(final ConstructorArgumentValues original) {
        this.indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>(0);
        this.genericArgumentValues = new LinkedList<ValueHolder>();
        this.addArgumentValues(original);
    }
    
    public void addArgumentValues(final ConstructorArgumentValues other) {
        if (other != null) {
            for (final Map.Entry<Integer, ValueHolder> entry : other.indexedArgumentValues.entrySet()) {
                this.addOrMergeIndexedArgumentValue(entry.getKey(), entry.getValue().copy());
            }
            for (final ValueHolder valueHolder : other.genericArgumentValues) {
                if (!this.genericArgumentValues.contains(valueHolder)) {
                    this.addOrMergeGenericArgumentValue(valueHolder.copy());
                }
            }
        }
    }
    
    public void addIndexedArgumentValue(final int index, final Object value) {
        this.addIndexedArgumentValue(index, new ValueHolder(value));
    }
    
    public void addIndexedArgumentValue(final int index, final Object value, final String type) {
        this.addIndexedArgumentValue(index, new ValueHolder(value, type));
    }
    
    public void addIndexedArgumentValue(final int index, final ValueHolder newValue) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        Assert.notNull(newValue, "ValueHolder must not be null");
        this.addOrMergeIndexedArgumentValue(index, newValue);
    }
    
    private void addOrMergeIndexedArgumentValue(final Integer key, final ValueHolder newValue) {
        final ValueHolder currentValue = this.indexedArgumentValues.get(key);
        if (currentValue != null && newValue.getValue() instanceof Mergeable) {
            final Mergeable mergeable = (Mergeable)newValue.getValue();
            if (mergeable.isMergeEnabled()) {
                newValue.setValue(mergeable.merge(currentValue.getValue()));
            }
        }
        this.indexedArgumentValues.put(key, newValue);
    }
    
    public boolean hasIndexedArgumentValue(final int index) {
        return this.indexedArgumentValues.containsKey(index);
    }
    
    public ValueHolder getIndexedArgumentValue(final int index, final Class<?> requiredType) {
        return this.getIndexedArgumentValue(index, requiredType, null);
    }
    
    public ValueHolder getIndexedArgumentValue(final int index, final Class<?> requiredType, final String requiredName) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        final ValueHolder valueHolder = this.indexedArgumentValues.get(index);
        if (valueHolder != null && (valueHolder.getType() == null || (requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) && (valueHolder.getName() == null || (requiredName != null && requiredName.equals(valueHolder.getName())))) {
            return valueHolder;
        }
        return null;
    }
    
    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends ValueHolder>)this.indexedArgumentValues);
    }
    
    public void addGenericArgumentValue(final Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }
    
    public void addGenericArgumentValue(final Object value, final String type) {
        this.genericArgumentValues.add(new ValueHolder(value, type));
    }
    
    public void addGenericArgumentValue(final ValueHolder newValue) {
        Assert.notNull(newValue, "ValueHolder must not be null");
        if (!this.genericArgumentValues.contains(newValue)) {
            this.addOrMergeGenericArgumentValue(newValue);
        }
    }
    
    private void addOrMergeGenericArgumentValue(final ValueHolder newValue) {
        if (newValue.getName() != null) {
            final Iterator<ValueHolder> it = this.genericArgumentValues.iterator();
            while (it.hasNext()) {
                final ValueHolder currentValue = it.next();
                if (newValue.getName().equals(currentValue.getName())) {
                    if (newValue.getValue() instanceof Mergeable) {
                        final Mergeable mergeable = (Mergeable)newValue.getValue();
                        if (mergeable.isMergeEnabled()) {
                            newValue.setValue(mergeable.merge(currentValue.getValue()));
                        }
                    }
                    it.remove();
                }
            }
        }
        this.genericArgumentValues.add(newValue);
    }
    
    public ValueHolder getGenericArgumentValue(final Class<?> requiredType) {
        return this.getGenericArgumentValue(requiredType, null, null);
    }
    
    public ValueHolder getGenericArgumentValue(final Class<?> requiredType, final String requiredName) {
        return this.getGenericArgumentValue(requiredType, requiredName, null);
    }
    
    public ValueHolder getGenericArgumentValue(final Class<?> requiredType, final String requiredName, final Set<ValueHolder> usedValueHolders) {
        for (final ValueHolder valueHolder : this.genericArgumentValues) {
            if (usedValueHolders != null && usedValueHolders.contains(valueHolder)) {
                continue;
            }
            if (valueHolder.getName() != null) {
                if (requiredName == null) {
                    continue;
                }
                if (!valueHolder.getName().equals(requiredName)) {
                    continue;
                }
            }
            if (valueHolder.getType() != null) {
                if (requiredType == null) {
                    continue;
                }
                if (!ClassUtils.matchesTypeName(requiredType, valueHolder.getType())) {
                    continue;
                }
            }
            if (requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null && !ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
                continue;
            }
            return valueHolder;
        }
        return null;
    }
    
    public List<ValueHolder> getGenericArgumentValues() {
        return Collections.unmodifiableList((List<? extends ValueHolder>)this.genericArgumentValues);
    }
    
    public ValueHolder getArgumentValue(final int index, final Class<?> requiredType) {
        return this.getArgumentValue(index, requiredType, null, null);
    }
    
    public ValueHolder getArgumentValue(final int index, final Class<?> requiredType, final String requiredName) {
        return this.getArgumentValue(index, requiredType, requiredName, null);
    }
    
    public ValueHolder getArgumentValue(final int index, final Class<?> requiredType, final String requiredName, final Set<ValueHolder> usedValueHolders) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = this.getIndexedArgumentValue(index, requiredType, requiredName);
        if (valueHolder == null) {
            valueHolder = this.getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
        }
        return valueHolder;
    }
    
    public int getArgumentCount() {
        return this.indexedArgumentValues.size() + this.genericArgumentValues.size();
    }
    
    public boolean isEmpty() {
        return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
    }
    
    public void clear() {
        this.indexedArgumentValues.clear();
        this.genericArgumentValues.clear();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConstructorArgumentValues)) {
            return false;
        }
        final ConstructorArgumentValues that = (ConstructorArgumentValues)other;
        if (this.genericArgumentValues.size() != that.genericArgumentValues.size() || this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
            return false;
        }
        final Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
        final Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            final ValueHolder vh1 = it1.next();
            final ValueHolder vh2 = it2.next();
            if (!vh1.contentEquals(vh2)) {
                return false;
            }
        }
        for (final Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
            final ValueHolder vh3 = entry.getValue();
            final ValueHolder vh4 = that.indexedArgumentValues.get(entry.getKey());
            if (!vh3.contentEquals(vh4)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 7;
        for (final ValueHolder valueHolder : this.genericArgumentValues) {
            hashCode = 31 * hashCode + valueHolder.contentHashCode();
        }
        hashCode *= 29;
        for (final Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
            hashCode = 31 * hashCode + (entry.getValue().contentHashCode() ^ entry.getKey().hashCode());
        }
        return hashCode;
    }
    
    public static class ValueHolder implements BeanMetadataElement
    {
        private Object value;
        private String type;
        private String name;
        private Object source;
        private boolean converted;
        private Object convertedValue;
        
        public ValueHolder(final Object value) {
            this.converted = false;
            this.value = value;
        }
        
        public ValueHolder(final Object value, final String type) {
            this.converted = false;
            this.value = value;
            this.type = type;
        }
        
        public ValueHolder(final Object value, final String type, final String name) {
            this.converted = false;
            this.value = value;
            this.type = type;
            this.name = name;
        }
        
        public void setValue(final Object value) {
            this.value = value;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public String getType() {
            return this.type;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setSource(final Object source) {
            this.source = source;
        }
        
        @Override
        public Object getSource() {
            return this.source;
        }
        
        public synchronized boolean isConverted() {
            return this.converted;
        }
        
        public synchronized void setConvertedValue(final Object value) {
            this.converted = true;
            this.convertedValue = value;
        }
        
        public synchronized Object getConvertedValue() {
            return this.convertedValue;
        }
        
        private boolean contentEquals(final ValueHolder other) {
            return this == other || (ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type));
        }
        
        private int contentHashCode() {
            return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
        }
        
        public ValueHolder copy() {
            final ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
            copy.setSource(this.source);
            return copy;
        }
    }
}
