// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Collection;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.util.HashMap;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public final class PropertyBasedCreator
{
    protected final int _propertyCount;
    protected final ValueInstantiator _valueInstantiator;
    protected final HashMap<String, SettableBeanProperty> _propertyLookup;
    protected final SettableBeanProperty[] _allProperties;
    
    protected PropertyBasedCreator(final DeserializationContext ctxt, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] creatorProps, final boolean caseInsensitive, final boolean addAliases) {
        this._valueInstantiator = valueInstantiator;
        if (caseInsensitive) {
            this._propertyLookup = new CaseInsensitiveMap();
        }
        else {
            this._propertyLookup = new HashMap<String, SettableBeanProperty>();
        }
        final int len = creatorProps.length;
        this._propertyCount = len;
        this._allProperties = new SettableBeanProperty[len];
        if (addAliases) {
            final DeserializationConfig config = ctxt.getConfig();
            for (final SettableBeanProperty prop : creatorProps) {
                if (!prop.isIgnorable()) {
                    final List<PropertyName> aliases = prop.findAliases(config);
                    if (!aliases.isEmpty()) {
                        for (final PropertyName pn : aliases) {
                            this._propertyLookup.put(pn.getSimpleName(), prop);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < len; ++i) {
            final SettableBeanProperty prop2 = creatorProps[i];
            this._allProperties[i] = prop2;
            if (!prop2.isIgnorable()) {
                this._propertyLookup.put(prop2.getName(), prop2);
            }
        }
    }
    
    public static PropertyBasedCreator construct(final DeserializationContext ctxt, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] srcCreatorProps, final BeanPropertyMap allProperties) throws JsonMappingException {
        final int len = srcCreatorProps.length;
        final SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
        for (int i = 0; i < len; ++i) {
            SettableBeanProperty prop = srcCreatorProps[i];
            if (!prop.hasValueDeserializer()) {
                prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
            }
            creatorProps[i] = prop;
        }
        return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, allProperties.isCaseInsensitive(), allProperties.hasAliases());
    }
    
    public static PropertyBasedCreator construct(final DeserializationContext ctxt, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] srcCreatorProps, final boolean caseInsensitive) throws JsonMappingException {
        final int len = srcCreatorProps.length;
        final SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
        for (int i = 0; i < len; ++i) {
            SettableBeanProperty prop = srcCreatorProps[i];
            if (!prop.hasValueDeserializer()) {
                prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
            }
            creatorProps[i] = prop;
        }
        return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, caseInsensitive, false);
    }
    
    @Deprecated
    public static PropertyBasedCreator construct(final DeserializationContext ctxt, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] srcCreatorProps) throws JsonMappingException {
        return construct(ctxt, valueInstantiator, srcCreatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
    }
    
    public Collection<SettableBeanProperty> properties() {
        return this._propertyLookup.values();
    }
    
    public SettableBeanProperty findCreatorProperty(final String name) {
        return this._propertyLookup.get(name);
    }
    
    public SettableBeanProperty findCreatorProperty(final int propertyIndex) {
        for (final SettableBeanProperty prop : this._propertyLookup.values()) {
            if (prop.getPropertyIndex() == propertyIndex) {
                return prop;
            }
        }
        return null;
    }
    
    public PropertyValueBuffer startBuilding(final JsonParser p, final DeserializationContext ctxt, final ObjectIdReader oir) {
        return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir);
    }
    
    public Object build(final DeserializationContext ctxt, final PropertyValueBuffer buffer) throws IOException {
        Object bean = this._valueInstantiator.createFromObjectWith(ctxt, this._allProperties, buffer);
        if (bean != null) {
            bean = buffer.handleIdValue(ctxt, bean);
            for (PropertyValue pv = buffer.buffered(); pv != null; pv = pv.next) {
                pv.assign(bean);
            }
        }
        return bean;
    }
    
    static class CaseInsensitiveMap extends HashMap<String, SettableBeanProperty>
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public SettableBeanProperty get(final Object key0) {
            return super.get(((String)key0).toLowerCase());
        }
        
        @Override
        public SettableBeanProperty put(String key, final SettableBeanProperty value) {
            key = key.toLowerCase();
            return super.put(key, value);
        }
    }
}
