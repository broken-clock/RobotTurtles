// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.util.List;

public class UnwrappedPropertyHandler
{
    protected final List<SettableBeanProperty> _properties;
    
    public UnwrappedPropertyHandler() {
        this._properties = new ArrayList<SettableBeanProperty>();
    }
    
    protected UnwrappedPropertyHandler(final List<SettableBeanProperty> props) {
        this._properties = props;
    }
    
    public void addProperty(final SettableBeanProperty property) {
        this._properties.add(property);
    }
    
    public UnwrappedPropertyHandler renameAll(final NameTransformer transformer) {
        final ArrayList<SettableBeanProperty> newProps = new ArrayList<SettableBeanProperty>(this._properties.size());
        for (SettableBeanProperty prop : this._properties) {
            final String newName = transformer.transform(prop.getName());
            prop = prop.withSimpleName(newName);
            final JsonDeserializer<?> deser = prop.getValueDeserializer();
            if (deser != null) {
                final JsonDeserializer<Object> newDeser = (JsonDeserializer<Object>)deser.unwrappingDeserializer(transformer);
                if (newDeser != deser) {
                    prop = prop.withValueDeserializer(newDeser);
                }
            }
            newProps.add(prop);
        }
        return new UnwrappedPropertyHandler(newProps);
    }
    
    public Object processUnwrapped(final JsonParser originalParser, final DeserializationContext ctxt, final Object bean, final TokenBuffer buffered) throws IOException {
        for (int i = 0, len = this._properties.size(); i < len; ++i) {
            final SettableBeanProperty prop = this._properties.get(i);
            final JsonParser p = buffered.asParser();
            p.nextToken();
            prop.deserializeAndSet(p, ctxt, bean);
        }
        return bean;
    }
}
