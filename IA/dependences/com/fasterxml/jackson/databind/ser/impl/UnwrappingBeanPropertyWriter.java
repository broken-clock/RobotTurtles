// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class UnwrappingBeanPropertyWriter extends BeanPropertyWriter implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final NameTransformer _nameTransformer;
    
    public UnwrappingBeanPropertyWriter(final BeanPropertyWriter base, final NameTransformer unwrapper) {
        super(base);
        this._nameTransformer = unwrapper;
    }
    
    protected UnwrappingBeanPropertyWriter(final UnwrappingBeanPropertyWriter base, final NameTransformer transformer, final SerializedString name) {
        super(base, name);
        this._nameTransformer = transformer;
    }
    
    @Override
    public UnwrappingBeanPropertyWriter rename(NameTransformer transformer) {
        final String oldName = this._name.getValue();
        final String newName = transformer.transform(oldName);
        transformer = NameTransformer.chainedTransformer(transformer, this._nameTransformer);
        return this._new(transformer, new SerializedString(newName));
    }
    
    protected UnwrappingBeanPropertyWriter _new(final NameTransformer transformer, final SerializedString newName) {
        return new UnwrappingBeanPropertyWriter(this, transformer, newName);
    }
    
    @Override
    public boolean isUnwrapping() {
        return true;
    }
    
    @Override
    public void serializeAsField(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
        final Object value = this.get(bean);
        if (value == null) {
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (UnwrappingBeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        if (!ser.isUnwrappingSerializer()) {
            gen.writeFieldName(this._name);
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        }
        else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }
    
    @Override
    public void assignSerializer(JsonSerializer<Object> ser) {
        if (ser != null) {
            NameTransformer t = this._nameTransformer;
            if (ser.isUnwrappingSerializer() && ser instanceof UnwrappingBeanSerializer) {
                t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)ser)._nameTransformer);
            }
            ser = ser.unwrappingSerializer(t);
        }
        super.assignSerializer(ser);
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor visitor, final SerializerProvider provider) throws JsonMappingException {
        final JsonSerializer<Object> ser = provider.findValueSerializer(this.getType(), this).unwrappingSerializer(this._nameTransformer);
        if (ser.isUnwrappingSerializer()) {
            ser.acceptJsonFormatVisitor(new JsonFormatVisitorWrapper.Base(provider) {
                @Override
                public JsonObjectFormatVisitor expectObjectFormat(final JavaType type) throws JsonMappingException {
                    return visitor;
                }
            }, this.getType());
        }
        else {
            super.depositSchemaProperty(visitor, provider);
        }
    }
    
    @Override
    protected void _depositSchemaProperty(final ObjectNode propertiesNode, final JsonNode schemaNode) {
        final JsonNode props = schemaNode.get("properties");
        if (props != null) {
            final Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> entry = it.next();
                String name = entry.getKey();
                if (this._nameTransformer != null) {
                    name = this._nameTransformer.transform(name);
                }
                propertiesNode.set(name, entry.getValue());
            }
        }
    }
    
    @Override
    protected JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer;
        if (this._nonTrivialBaseType != null) {
            final JavaType subtype = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            serializer = provider.findValueSerializer(subtype, this);
        }
        else {
            serializer = provider.findValueSerializer(type, this);
        }
        NameTransformer t = this._nameTransformer;
        if (serializer.isUnwrappingSerializer() && serializer instanceof UnwrappingBeanSerializer) {
            t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)serializer)._nameTransformer);
        }
        serializer = serializer.unwrappingSerializer(t);
        this._dynamicSerializers = this._dynamicSerializers.newWith(type, serializer);
        return serializer;
    }
}
