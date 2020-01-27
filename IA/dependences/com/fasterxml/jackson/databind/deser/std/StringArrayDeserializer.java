// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JacksonStdImpl
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer
{
    private static final long serialVersionUID = 2L;
    private static final String[] NO_STRINGS;
    public static final StringArrayDeserializer instance;
    protected JsonDeserializer<String> _elementDeserializer;
    protected final NullValueProvider _nullProvider;
    protected final Boolean _unwrapSingle;
    protected final boolean _skipNullValues;
    
    public StringArrayDeserializer() {
        this(null, null, null);
    }
    
    protected StringArrayDeserializer(final JsonDeserializer<?> deser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(String[].class);
        this._elementDeserializer = (JsonDeserializer<String>)deser;
        this._nullProvider = nuller;
        this._unwrapSingle = unwrapSingle;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return StringArrayDeserializer.NO_STRINGS;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._elementDeserializer;
        deser = this.findConvertingContentDeserializer(ctxt, property, deser);
        final JavaType type = ctxt.constructType(String.class);
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(type, property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property, type);
        }
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, String[].class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        final NullValueProvider nuller = this.findContentNullProvider(ctxt, property, deser);
        if (deser != null && this.isDefaultDeserializer(deser)) {
            deser = null;
        }
        if (this._elementDeserializer == deser && this._unwrapSingle == unwrapSingle && this._nullProvider == nuller) {
            return this;
        }
        return new StringArrayDeserializer(deser, nuller, unwrapSingle);
    }
    
    @Override
    public String[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt);
        }
        if (this._elementDeserializer != null) {
            return this._deserializeCustom(p, ctxt, null);
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        try {
            while (true) {
                String value = p.nextTextValue();
                if (value == null) {
                    final JsonToken t = p.getCurrentToken();
                    if (t == JsonToken.END_ARRAY) {
                        break;
                    }
                    if (t == JsonToken.VALUE_NULL) {
                        if (this._skipNullValues) {
                            continue;
                        }
                        value = (String)this._nullProvider.getNullValue(ctxt);
                    }
                    else {
                        value = this._parseString(p, ctxt);
                    }
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
        final String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    protected final String[] _deserializeCustom(final JsonParser p, final DeserializationContext ctxt, final String[] old) throws IOException {
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        int ix;
        Object[] chunk;
        if (old == null) {
            ix = 0;
            chunk = buffer.resetAndStart();
        }
        else {
            ix = old.length;
            chunk = buffer.resetAndStart(old, ix);
        }
        final JsonDeserializer<String> deser = this._elementDeserializer;
        try {
            while (true) {
                String value;
                if (p.nextTextValue() == null) {
                    final JsonToken t = p.getCurrentToken();
                    if (t == JsonToken.END_ARRAY) {
                        break;
                    }
                    if (t == JsonToken.VALUE_NULL) {
                        if (this._skipNullValues) {
                            continue;
                        }
                        value = (String)this._nullProvider.getNullValue(ctxt);
                    }
                    else {
                        value = deser.deserialize(p, ctxt);
                    }
                }
                else {
                    value = deser.deserialize(p, ctxt);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
        }
        catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, String.class, ix);
        }
        final String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    @Override
    public String[] deserialize(final JsonParser p, final DeserializationContext ctxt, final String[] intoValue) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            final String[] arr = this.handleNonArray(p, ctxt);
            if (arr == null) {
                return intoValue;
            }
            final int offset = intoValue.length;
            final String[] result = new String[offset + arr.length];
            System.arraycopy(intoValue, 0, result, 0, offset);
            System.arraycopy(arr, 0, result, offset, arr.length);
            return result;
        }
        else {
            if (this._elementDeserializer != null) {
                return this._deserializeCustom(p, ctxt, intoValue);
            }
            final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            int ix = intoValue.length;
            Object[] chunk = buffer.resetAndStart(intoValue, ix);
            try {
                while (true) {
                    String value = p.nextTextValue();
                    if (value == null) {
                        final JsonToken t = p.getCurrentToken();
                        if (t == JsonToken.END_ARRAY) {
                            break;
                        }
                        if (t == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                return StringArrayDeserializer.NO_STRINGS;
                            }
                            value = (String)this._nullProvider.getNullValue(ctxt);
                        }
                        else {
                            value = this._parseString(p, ctxt);
                        }
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
            final String[] result2 = buffer.completeAndClearBuffer(chunk, ix, String.class);
            ctxt.returnObjectBuffer(buffer);
            return result2;
        }
    }
    
    private final String[] handleNonArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            final String value = (String)(p.hasToken(JsonToken.VALUE_NULL) ? this._nullProvider.getNullValue(ctxt) : this._parseString(p, ctxt));
            return new String[] { value };
        }
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            final String str = p.getText();
            if (str.length() == 0) {
                return null;
            }
        }
        return (String[])ctxt.handleUnexpectedToken(this._valueClass, p);
    }
    
    static {
        NO_STRINGS = new String[0];
        instance = new StringArrayDeserializer();
    }
}
