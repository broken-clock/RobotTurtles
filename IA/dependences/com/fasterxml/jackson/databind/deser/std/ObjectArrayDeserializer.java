// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.reflect.Array;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JacksonStdImpl
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object[]> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected static final Object[] NO_OBJECTS;
    protected final boolean _untyped;
    protected final Class<?> _elementClass;
    protected JsonDeserializer<Object> _elementDeserializer;
    protected final TypeDeserializer _elementTypeDeserializer;
    
    public ObjectArrayDeserializer(final JavaType arrayType, final JsonDeserializer<Object> elemDeser, final TypeDeserializer elemTypeDeser) {
        super(arrayType, null, null);
        this._elementClass = arrayType.getContentType().getRawClass();
        this._untyped = (this._elementClass == Object.class);
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
    }
    
    protected ObjectArrayDeserializer(final ObjectArrayDeserializer base, final JsonDeserializer<Object> elemDeser, final TypeDeserializer elemTypeDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(base, nuller, unwrapSingle);
        this._elementClass = base._elementClass;
        this._untyped = base._untyped;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
    }
    
    public ObjectArrayDeserializer withDeserializer(final TypeDeserializer elemTypeDeser, final JsonDeserializer<?> elemDeser) {
        return this.withResolved(elemTypeDeser, elemDeser, this._nullProvider, this._unwrapSingle);
    }
    
    public ObjectArrayDeserializer withResolved(final TypeDeserializer elemTypeDeser, final JsonDeserializer<?> elemDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        if (unwrapSingle == this._unwrapSingle && nuller == this._nullProvider && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer) {
            return this;
        }
        return new ObjectArrayDeserializer(this, (JsonDeserializer<Object>)elemDeser, elemTypeDeser, nuller, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._elementDeserializer == null && this._elementTypeDeserializer == null;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> valueDeser = this._elementDeserializer;
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, this._containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
        final JavaType vt = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
        if (elemTypeDeser != null) {
            elemTypeDeser = elemTypeDeser.forProperty(property);
        }
        final NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
        return this.withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._elementDeserializer;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return ObjectArrayDeserializer.NO_OBJECTS;
    }
    
    @Override
    public Object[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt);
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        final TypeDeserializer typeDeser = this._elementTypeDeserializer;
        try {
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                Object value;
                if (t == JsonToken.VALUE_NULL) {
                    if (this._skipNullValues) {
                        continue;
                    }
                    value = this._nullProvider.getNullValue(ctxt);
                }
                else if (typeDeser == null) {
                    value = this._elementDeserializer.deserialize(p, ctxt);
                }
                else {
                    value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
        }
        catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
        }
        Object[] result;
        if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
        }
        else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        }
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    @Override
    public Object[] deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return (Object[])typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    @Override
    public Object[] deserialize(final JsonParser p, final DeserializationContext ctxt, final Object[] intoValue) throws IOException {
        if (p.isExpectedStartArrayToken()) {
            final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            int ix = intoValue.length;
            Object[] chunk = buffer.resetAndStart(intoValue, ix);
            final TypeDeserializer typeDeser = this._elementTypeDeserializer;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    Object value;
                    if (t == JsonToken.VALUE_NULL) {
                        if (this._skipNullValues) {
                            continue;
                        }
                        value = this._nullProvider.getNullValue(ctxt);
                    }
                    else if (typeDeser == null) {
                        value = this._elementDeserializer.deserialize(p, ctxt);
                    }
                    else {
                        value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
                    }
                    if (ix >= chunk.length) {
                        chunk = buffer.appendCompletedChunk(chunk);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
            }
            Object[] result;
            if (this._untyped) {
                result = buffer.completeAndClearBuffer(chunk, ix);
            }
            else {
                result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
            }
            ctxt.returnObjectBuffer(buffer);
            return result;
        }
        final Object[] arr = this.handleNonArray(p, ctxt);
        if (arr == null) {
            return intoValue;
        }
        final int offset = intoValue.length;
        final Object[] result2 = new Object[offset + arr.length];
        System.arraycopy(intoValue, 0, result2, 0, offset);
        System.arraycopy(arr, 0, result2, offset, arr.length);
        return result2;
    }
    
    protected Byte[] deserializeFromBase64(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
        final Byte[] result = new Byte[b.length];
        for (int i = 0, len = b.length; i < len; ++i) {
            result[i] = b[i];
        }
        return result;
    }
    
    protected Object[] handleNonArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            final String str = p.getText();
            if (str.length() == 0) {
                return null;
            }
        }
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            final JsonToken t = p.getCurrentToken();
            Object value;
            if (t == JsonToken.VALUE_NULL) {
                if (this._skipNullValues) {
                    return ObjectArrayDeserializer.NO_OBJECTS;
                }
                value = this._nullProvider.getNullValue(ctxt);
            }
            else if (this._elementTypeDeserializer == null) {
                value = this._elementDeserializer.deserialize(p, ctxt);
            }
            else {
                value = this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
            }
            Object[] result;
            if (this._untyped) {
                result = new Object[] { null };
            }
            else {
                result = (Object[])Array.newInstance(this._elementClass, 1);
            }
            result[0] = value;
            return result;
        }
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_STRING && this._elementClass == Byte.class) {
            return this.deserializeFromBase64(p, ctxt);
        }
        return (Object[])ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p);
    }
    
    static {
        NO_OBJECTS = new Object[0];
    }
}
