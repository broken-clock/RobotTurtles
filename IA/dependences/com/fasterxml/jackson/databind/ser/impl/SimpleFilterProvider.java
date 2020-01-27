// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import java.util.Iterator;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import java.util.Map;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ser.FilterProvider;

public class SimpleFilterProvider extends FilterProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Map<String, PropertyFilter> _filtersById;
    protected PropertyFilter _defaultFilter;
    protected boolean _cfgFailOnUnknownId;
    
    public SimpleFilterProvider() {
        this(new HashMap<String, Object>());
    }
    
    public SimpleFilterProvider(final Map<String, ?> mapping) {
        this._cfgFailOnUnknownId = true;
        for (final Object ob : mapping.values()) {
            if (!(ob instanceof PropertyFilter)) {
                this._filtersById = _convert(mapping);
                return;
            }
        }
        this._filtersById = (Map<String, PropertyFilter>)mapping;
    }
    
    private static final Map<String, PropertyFilter> _convert(final Map<String, ?> filters) {
        final HashMap<String, PropertyFilter> result = new HashMap<String, PropertyFilter>();
        for (final Map.Entry<String, ?> entry : filters.entrySet()) {
            final Object f = entry.getValue();
            if (f instanceof PropertyFilter) {
                result.put(entry.getKey(), (PropertyFilter)f);
            }
            else {
                if (!(f instanceof BeanPropertyFilter)) {
                    throw new IllegalArgumentException("Unrecognized filter type (" + f.getClass().getName() + ")");
                }
                result.put(entry.getKey(), _convert((BeanPropertyFilter)f));
            }
        }
        return result;
    }
    
    private static final PropertyFilter _convert(final BeanPropertyFilter f) {
        return SimpleBeanPropertyFilter.from(f);
    }
    
    @Deprecated
    public SimpleFilterProvider setDefaultFilter(final BeanPropertyFilter f) {
        this._defaultFilter = SimpleBeanPropertyFilter.from(f);
        return this;
    }
    
    public SimpleFilterProvider setDefaultFilter(final PropertyFilter f) {
        this._defaultFilter = f;
        return this;
    }
    
    public SimpleFilterProvider setDefaultFilter(final SimpleBeanPropertyFilter f) {
        this._defaultFilter = f;
        return this;
    }
    
    public PropertyFilter getDefaultFilter() {
        return this._defaultFilter;
    }
    
    public SimpleFilterProvider setFailOnUnknownId(final boolean state) {
        this._cfgFailOnUnknownId = state;
        return this;
    }
    
    public boolean willFailOnUnknownId() {
        return this._cfgFailOnUnknownId;
    }
    
    @Deprecated
    public SimpleFilterProvider addFilter(final String id, final BeanPropertyFilter filter) {
        this._filtersById.put(id, _convert(filter));
        return this;
    }
    
    public SimpleFilterProvider addFilter(final String id, final PropertyFilter filter) {
        this._filtersById.put(id, filter);
        return this;
    }
    
    public SimpleFilterProvider addFilter(final String id, final SimpleBeanPropertyFilter filter) {
        this._filtersById.put(id, filter);
        return this;
    }
    
    public PropertyFilter removeFilter(final String id) {
        return this._filtersById.remove(id);
    }
    
    @Deprecated
    @Override
    public BeanPropertyFilter findFilter(final Object filterId) {
        throw new UnsupportedOperationException("Access to deprecated filters not supported");
    }
    
    @Override
    public PropertyFilter findPropertyFilter(final Object filterId, final Object valueToFilter) {
        PropertyFilter f = this._filtersById.get(filterId);
        if (f == null) {
            f = this._defaultFilter;
            if (f == null && this._cfgFailOnUnknownId) {
                throw new IllegalArgumentException("No filter configured with id '" + filterId + "' (type " + filterId.getClass().getName() + ")");
            }
        }
        return f;
    }
}
