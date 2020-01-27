// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.AbstractMap;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Map;

@JacksonStdImpl
public class MapEntryDeserializer extends ContainerDeserializerBase<Map.Entry<Object, Object>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final KeyDeserializer _keyDeserializer;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    
    public MapEntryDeserializer(final JavaType type, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser) {
        super(type);
        if (type.containedTypeCount() != 2) {
            throw new IllegalArgumentException("Missing generic type information for " + type);
        }
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
    }
    
    protected MapEntryDeserializer(final MapEntryDeserializer src) {
        super(src);
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
    }
    
    protected MapEntryDeserializer(final MapEntryDeserializer src, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser) {
        super(src);
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
    }
    
    protected MapEntryDeserializer withResolved(final KeyDeserializer keyDeser, final TypeDeserializer valueTypeDeser, final JsonDeserializer<?> valueDeser) {
        if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser) {
            return this;
        }
        return new MapEntryDeserializer(this, keyDeser, (JsonDeserializer<Object>)valueDeser, valueTypeDeser);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        KeyDeserializer kd = this._keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(this._containerType.containedType(0), property);
        }
        else if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
        }
        JsonDeserializer<?> vd = this._valueDeserializer;
        vd = this.findConvertingContentDeserializer(ctxt, property, vd);
        final JavaType contentType = this._containerType.containedType(1);
        if (vd == null) {
            vd = ctxt.findContextualValueDeserializer(contentType, property);
        }
        else {
            vd = ctxt.handleSecondaryContextualization(vd, property, contentType);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return this.withResolved(kd, vtd, vd);
    }
    
    @Override
    public JavaType getContentType() {
        return this._containerType.containedType(1);
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public Map.Entry<Object, Object> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            return this._deserializeFromEmpty(p, ctxt);
        }
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        if (t != JsonToken.FIELD_NAME) {
            if (t == JsonToken.END_OBJECT) {
                return ctxt.reportInputMismatch(this, "Cannot deserialize a Map.Entry out of empty JSON Object", new Object[0]);
            }
            return (Map.Entry<Object, Object>)ctxt.handleUnexpectedToken(this.handledType(), p);
        }
        else {
            final KeyDeserializer keyDes = this._keyDeserializer;
            final JsonDeserializer<Object> valueDes = this._valueDeserializer;
            final TypeDeserializer typeDeser = this._valueTypeDeserializer;
            final String keyStr = p.getCurrentName();
            final Object key = keyDes.deserializeKey(keyStr, ctxt);
            Object value = null;
            t = p.nextToken();
            try {
                if (t == JsonToken.VALUE_NULL) {
                    value = valueDes.getNullValue(ctxt);
                }
                else if (typeDeser == null) {
                    value = valueDes.deserialize(p, ctxt);
                }
                else {
                    value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                }
            }
            catch (Exception e) {
                ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e, Map.Entry.class, keyStr);
            }
            t = p.nextToken();
            if (t != JsonToken.END_OBJECT) {
                if (t == JsonToken.FIELD_NAME) {
                    ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: more than one entry in JSON (second field: '%s')", p.getCurrentName());
                }
                else {
                    ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: unexpected content after JSON Object entry: " + t, new Object[0]);
                }
                return null;
            }
            return new AbstractMap.SimpleEntry<Object, Object>(key, value);
        }
    }
    
    @Override
    public Map.Entry<Object, Object> deserialize(final JsonParser p, final DeserializationContext ctxt, final Map.Entry<Object, Object> result) throws IOException {
        throw new IllegalStateException("Cannot update Map.Entry values");
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
}
