// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.util.Map;
import com.fasterxml.jackson.databind.JavaType;

public class ExternalTypeHandler
{
    private final JavaType _beanType;
    private final ExtTypedProperty[] _properties;
    private final Map<String, Object> _nameToPropertyIndex;
    private final String[] _typeIds;
    private final TokenBuffer[] _tokens;
    
    protected ExternalTypeHandler(final JavaType beanType, final ExtTypedProperty[] properties, final Map<String, Object> nameToPropertyIndex, final String[] typeIds, final TokenBuffer[] tokens) {
        this._beanType = beanType;
        this._properties = properties;
        this._nameToPropertyIndex = nameToPropertyIndex;
        this._typeIds = typeIds;
        this._tokens = tokens;
    }
    
    protected ExternalTypeHandler(final ExternalTypeHandler h) {
        this._beanType = h._beanType;
        this._properties = h._properties;
        this._nameToPropertyIndex = h._nameToPropertyIndex;
        final int len = this._properties.length;
        this._typeIds = new String[len];
        this._tokens = new TokenBuffer[len];
    }
    
    public static Builder builder(final JavaType beanType) {
        return new Builder(beanType);
    }
    
    public ExternalTypeHandler start() {
        return new ExternalTypeHandler(this);
    }
    
    public boolean handleTypePropertyValue(final JsonParser p, final DeserializationContext ctxt, final String propName, final Object bean) throws IOException {
        final Object ob = this._nameToPropertyIndex.get(propName);
        if (ob == null) {
            return false;
        }
        final String typeId = p.getText();
        if (ob instanceof List) {
            boolean result = false;
            for (final Integer index : (List)ob) {
                if (this._handleTypePropertyValue(p, ctxt, propName, bean, typeId, index)) {
                    result = true;
                }
            }
            return result;
        }
        return this._handleTypePropertyValue(p, ctxt, propName, bean, typeId, (int)ob);
    }
    
    private final boolean _handleTypePropertyValue(final JsonParser p, final DeserializationContext ctxt, final String propName, final Object bean, final String typeId, final int index) throws IOException {
        final ExtTypedProperty prop = this._properties[index];
        if (!prop.hasTypePropertyName(propName)) {
            return false;
        }
        final boolean canDeserialize = bean != null && this._tokens[index] != null;
        if (canDeserialize) {
            this._deserializeAndSet(p, ctxt, bean, index, typeId);
            this._tokens[index] = null;
        }
        else {
            this._typeIds[index] = typeId;
        }
        return true;
    }
    
    public boolean handlePropertyValue(final JsonParser p, final DeserializationContext ctxt, final String propName, final Object bean) throws IOException {
        final Object ob = this._nameToPropertyIndex.get(propName);
        if (ob == null) {
            return false;
        }
        if (ob instanceof List) {
            final Iterator<Integer> it = ((List)ob).iterator();
            final Integer index = it.next();
            final ExtTypedProperty prop = this._properties[index];
            if (prop.hasTypePropertyName(propName)) {
                final String typeId = p.getText();
                p.skipChildren();
                this._typeIds[index] = typeId;
                while (it.hasNext()) {
                    this._typeIds[it.next()] = typeId;
                }
            }
            else {
                final TokenBuffer tokens = new TokenBuffer(p, ctxt);
                tokens.copyCurrentStructure(p);
                this._tokens[index] = tokens;
                while (it.hasNext()) {
                    this._tokens[it.next()] = tokens;
                }
            }
            return true;
        }
        final int index2 = (int)ob;
        final ExtTypedProperty prop2 = this._properties[index2];
        boolean canDeserialize;
        if (prop2.hasTypePropertyName(propName)) {
            this._typeIds[index2] = p.getText();
            p.skipChildren();
            canDeserialize = (bean != null && this._tokens[index2] != null);
        }
        else {
            final TokenBuffer tokens = new TokenBuffer(p, ctxt);
            tokens.copyCurrentStructure(p);
            this._tokens[index2] = tokens;
            canDeserialize = (bean != null && this._typeIds[index2] != null);
        }
        if (canDeserialize) {
            final String typeId = this._typeIds[index2];
            this._typeIds[index2] = null;
            this._deserializeAndSet(p, ctxt, bean, index2, typeId);
            this._tokens[index2] = null;
        }
        return true;
    }
    
    public Object complete(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        for (int i = 0, len = this._properties.length; i < len; ++i) {
            String typeId = this._typeIds[i];
            if (typeId == null) {
                final TokenBuffer tokens = this._tokens[i];
                if (tokens == null) {
                    continue;
                }
                final JsonToken t = tokens.firstToken();
                if (t.isScalarValue()) {
                    final JsonParser buffered = tokens.asParser(p);
                    buffered.nextToken();
                    final SettableBeanProperty extProp = this._properties[i].getProperty();
                    final Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
                    if (result != null) {
                        extProp.set(bean, result);
                        continue;
                    }
                    if (!this._properties[i].hasDefaultType()) {
                        ctxt.reportInputMismatch(bean.getClass(), "Missing external type id property '%s'", this._properties[i].getTypePropertyName());
                    }
                    else {
                        typeId = this._properties[i].getDefaultTypeId();
                    }
                }
            }
            else if (this._tokens[i] == null) {
                final SettableBeanProperty prop = this._properties[i].getProperty();
                if (prop.isRequired() || ctxt.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)) {
                    ctxt.reportInputMismatch(bean.getClass(), "Missing property '%s' for external type id '%s'", prop.getName(), this._properties[i].getTypePropertyName());
                }
                return bean;
            }
            this._deserializeAndSet(p, ctxt, bean, i, typeId);
        }
        return bean;
    }
    
    public Object complete(final JsonParser p, final DeserializationContext ctxt, final PropertyValueBuffer buffer, final PropertyBasedCreator creator) throws IOException {
        final int len = this._properties.length;
        final Object[] values = new Object[len];
        for (int i = 0; i < len; ++i) {
            String typeId = this._typeIds[i];
            final ExtTypedProperty extProp = this._properties[i];
            if (typeId == null) {
                if (this._tokens[i] == null) {
                    continue;
                }
                if (!extProp.hasDefaultType()) {
                    ctxt.reportInputMismatch(this._beanType, "Missing external type id property '%s'", extProp.getTypePropertyName());
                }
                else {
                    typeId = extProp.getDefaultTypeId();
                }
            }
            else if (this._tokens[i] == null) {
                final SettableBeanProperty prop = extProp.getProperty();
                ctxt.reportInputMismatch(this._beanType, "Missing property '%s' for external type id '%s'", prop.getName(), this._properties[i].getTypePropertyName());
            }
            values[i] = this._deserialize(p, ctxt, i, typeId);
            final SettableBeanProperty prop = extProp.getProperty();
            if (prop.getCreatorIndex() >= 0) {
                buffer.assignParameter(prop, values[i]);
                final SettableBeanProperty typeProp = extProp.getTypeProperty();
                if (typeProp != null && typeProp.getCreatorIndex() >= 0) {
                    Object v;
                    if (typeProp.getType().hasRawClass(String.class)) {
                        v = typeId;
                    }
                    else {
                        final TokenBuffer tb = new TokenBuffer(p, ctxt);
                        tb.writeString(typeId);
                        v = typeProp.getValueDeserializer().deserialize(tb.asParserOnFirstToken(), ctxt);
                        tb.close();
                    }
                    buffer.assignParameter(typeProp, v);
                }
            }
        }
        final Object bean = creator.build(ctxt, buffer);
        for (int j = 0; j < len; ++j) {
            final SettableBeanProperty prop2 = this._properties[j].getProperty();
            if (prop2.getCreatorIndex() < 0) {
                prop2.set(bean, values[j]);
            }
        }
        return bean;
    }
    
    protected final Object _deserialize(final JsonParser p, final DeserializationContext ctxt, final int index, final String typeId) throws IOException {
        final JsonParser p2 = this._tokens[index].asParser(p);
        final JsonToken t = p2.nextToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        final TokenBuffer merged = new TokenBuffer(p, ctxt);
        merged.writeStartArray();
        merged.writeString(typeId);
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        final JsonParser mp = merged.asParser(p);
        mp.nextToken();
        return this._properties[index].getProperty().deserialize(mp, ctxt);
    }
    
    protected final void _deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object bean, final int index, final String typeId) throws IOException {
        final JsonParser p2 = this._tokens[index].asParser(p);
        final JsonToken t = p2.nextToken();
        if (t == JsonToken.VALUE_NULL) {
            this._properties[index].getProperty().set(bean, null);
            return;
        }
        final TokenBuffer merged = new TokenBuffer(p, ctxt);
        merged.writeStartArray();
        merged.writeString(typeId);
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        final JsonParser mp = merged.asParser(p);
        mp.nextToken();
        this._properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
    }
    
    public static class Builder
    {
        private final JavaType _beanType;
        private final List<ExtTypedProperty> _properties;
        private final Map<String, Object> _nameToPropertyIndex;
        
        protected Builder(final JavaType t) {
            this._properties = new ArrayList<ExtTypedProperty>();
            this._nameToPropertyIndex = new HashMap<String, Object>();
            this._beanType = t;
        }
        
        public void addExternal(final SettableBeanProperty property, final TypeDeserializer typeDeser) {
            final Integer index = this._properties.size();
            this._properties.add(new ExtTypedProperty(property, typeDeser));
            this._addPropertyIndex(property.getName(), index);
            this._addPropertyIndex(typeDeser.getPropertyName(), index);
        }
        
        private void _addPropertyIndex(final String name, final Integer index) {
            final Object ob = this._nameToPropertyIndex.get(name);
            if (ob == null) {
                this._nameToPropertyIndex.put(name, index);
            }
            else if (ob instanceof List) {
                final List<Object> list = (List<Object>)ob;
                list.add(index);
            }
            else {
                final List<Object> list = new LinkedList<Object>();
                list.add(ob);
                list.add(index);
                this._nameToPropertyIndex.put(name, list);
            }
        }
        
        public ExternalTypeHandler build(final BeanPropertyMap otherProps) {
            final int len = this._properties.size();
            final ExtTypedProperty[] extProps = new ExtTypedProperty[len];
            for (int i = 0; i < len; ++i) {
                final ExtTypedProperty extProp = this._properties.get(i);
                final String typePropId = extProp.getTypePropertyName();
                final SettableBeanProperty typeProp = otherProps.find(typePropId);
                if (typeProp != null) {
                    extProp.linkTypeProperty(typeProp);
                }
                extProps[i] = extProp;
            }
            return new ExternalTypeHandler(this._beanType, extProps, this._nameToPropertyIndex, null, null);
        }
    }
    
    private static final class ExtTypedProperty
    {
        private final SettableBeanProperty _property;
        private final TypeDeserializer _typeDeserializer;
        private final String _typePropertyName;
        private SettableBeanProperty _typeProperty;
        
        public ExtTypedProperty(final SettableBeanProperty property, final TypeDeserializer typeDeser) {
            this._property = property;
            this._typeDeserializer = typeDeser;
            this._typePropertyName = typeDeser.getPropertyName();
        }
        
        public void linkTypeProperty(final SettableBeanProperty p) {
            this._typeProperty = p;
        }
        
        public boolean hasTypePropertyName(final String n) {
            return n.equals(this._typePropertyName);
        }
        
        public boolean hasDefaultType() {
            return this._typeDeserializer.getDefaultImpl() != null;
        }
        
        public String getDefaultTypeId() {
            final Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
            if (defaultType == null) {
                return null;
            }
            return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
        }
        
        public String getTypePropertyName() {
            return this._typePropertyName;
        }
        
        public SettableBeanProperty getProperty() {
            return this._property;
        }
        
        public SettableBeanProperty getTypeProperty() {
            return this._typeProperty;
        }
    }
}
