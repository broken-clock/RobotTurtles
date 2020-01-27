// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.EnumSet;

public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _enumType;
    protected final Class<Enum> _enumClass;
    protected JsonDeserializer<Enum<?>> _enumDeserializer;
    protected final Boolean _unwrapSingle;
    
    public EnumSetDeserializer(final JavaType enumType, final JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumType = enumType;
        this._enumClass = (Class<Enum>)enumType.getRawClass();
        if (!this._enumClass.isEnum()) {
            throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
        }
        this._enumDeserializer = (JsonDeserializer<Enum<?>>)deser;
        this._unwrapSingle = null;
    }
    
    protected EnumSetDeserializer(final EnumSetDeserializer base, final JsonDeserializer<?> deser, final Boolean unwrapSingle) {
        super(base);
        this._enumType = base._enumType;
        this._enumClass = base._enumClass;
        this._enumDeserializer = (JsonDeserializer<Enum<?>>)deser;
        this._unwrapSingle = unwrapSingle;
    }
    
    public EnumSetDeserializer withDeserializer(final JsonDeserializer<?> deser) {
        if (this._enumDeserializer == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, this._unwrapSingle);
    }
    
    public EnumSetDeserializer withResolved(final JsonDeserializer<?> deser, final Boolean unwrapSingle) {
        if (this._unwrapSingle == unwrapSingle && this._enumDeserializer == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._enumType.getValueHandler() == null;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> deser = this._enumDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._enumType, property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property, this._enumType);
        }
        return this.withResolved(deser, unwrapSingle);
    }
    
    @Override
    public EnumSet<?> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final EnumSet result = this.constructSet();
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        return this._deserialize(p, ctxt, result);
    }
    
    @Override
    public EnumSet<?> deserialize(final JsonParser p, final DeserializationContext ctxt, final EnumSet<?> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        return this._deserialize(p, ctxt, result);
    }
    
    protected final EnumSet<?> _deserialize(final JsonParser p, final DeserializationContext ctxt, final EnumSet result) throws IOException {
        try {
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                if (t == JsonToken.VALUE_NULL) {
                    return (EnumSet<?>)ctxt.handleUnexpectedToken(this._enumClass, p);
                }
                final Enum<?> value = this._enumDeserializer.deserialize(p, ctxt);
                if (value == null) {
                    continue;
                }
                result.add(value);
            }
        }
        catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
        return (EnumSet<?>)result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumClass);
    }
    
    protected EnumSet<?> handleNonArray(final JsonParser p, final DeserializationContext ctxt, final EnumSet result) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (EnumSet<?>)ctxt.handleUnexpectedToken(EnumSet.class, p);
        }
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return (EnumSet<?>)ctxt.handleUnexpectedToken(this._enumClass, p);
        }
        try {
            final Enum<?> value = this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
                result.add(value);
            }
        }
        catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
        return (EnumSet<?>)result;
    }
}
