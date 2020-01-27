// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Collection;
import java.beans.PropertyEditorSupport;

public class CustomCollectionEditor extends PropertyEditorSupport
{
    private final Class<? extends Collection> collectionType;
    private final boolean nullAsEmptyCollection;
    
    public CustomCollectionEditor(final Class<? extends Collection> collectionType) {
        this(collectionType, false);
    }
    
    public CustomCollectionEditor(final Class<? extends Collection> collectionType, final boolean nullAsEmptyCollection) {
        if (collectionType == null) {
            throw new IllegalArgumentException("Collection type is required");
        }
        if (!Collection.class.isAssignableFrom(collectionType)) {
            throw new IllegalArgumentException("Collection type [" + collectionType.getName() + "] does not implement [java.util.Collection]");
        }
        this.collectionType = collectionType;
        this.nullAsEmptyCollection = nullAsEmptyCollection;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(text);
    }
    
    @Override
    public void setValue(final Object value) {
        if (value == null && this.nullAsEmptyCollection) {
            super.setValue(this.createCollection(this.collectionType, 0));
        }
        else if (value == null || (this.collectionType.isInstance(value) && !this.alwaysCreateNewCollection())) {
            super.setValue(value);
        }
        else if (value instanceof Collection) {
            final Collection<?> source = (Collection<?>)value;
            final Collection<Object> target = this.createCollection(this.collectionType, source.size());
            for (final Object elem : source) {
                target.add(this.convertElement(elem));
            }
            super.setValue(target);
        }
        else if (value.getClass().isArray()) {
            final int length = Array.getLength(value);
            final Collection<Object> target = this.createCollection(this.collectionType, length);
            for (int i = 0; i < length; ++i) {
                target.add(this.convertElement(Array.get(value, i)));
            }
            super.setValue(target);
        }
        else {
            final Collection<Object> target2 = this.createCollection(this.collectionType, 1);
            target2.add(this.convertElement(value));
            super.setValue(target2);
        }
    }
    
    protected Collection<Object> createCollection(final Class<? extends Collection> collectionType, final int initialCapacity) {
        if (!collectionType.isInterface()) {
            try {
                return (Collection<Object>)collectionType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate collection class [" + collectionType.getName() + "]: " + ex.getMessage());
            }
        }
        if (List.class.equals(collectionType)) {
            return new ArrayList<Object>(initialCapacity);
        }
        if (SortedSet.class.equals(collectionType)) {
            return new TreeSet<Object>();
        }
        return new LinkedHashSet<Object>(initialCapacity);
    }
    
    protected boolean alwaysCreateNewCollection() {
        return false;
    }
    
    protected Object convertElement(final Object element) {
        return element;
    }
    
    @Override
    public String getAsText() {
        return null;
    }
}
