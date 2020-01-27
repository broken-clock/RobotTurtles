// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public abstract class ReferenceTypeDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer
{
    private static final long serialVersionUID = 2L;
    protected final JavaType _fullType;
    protected final ValueInstantiator _valueInstantiator;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final JsonDeserializer<Object> _valueDeserializer;
    
    public ReferenceTypeDeserializer(final JavaType fullType, final ValueInstantiator vi, final TypeDeserializer typeDeser, final JsonDeserializer<?> deser) {
        super(fullType);
        this._valueInstantiator = vi;
        this._fullType = fullType;
        this._valueDeserializer = (JsonDeserializer<Object>)deser;
        this._valueTypeDeserializer = typeDeser;
    }
    
    @Deprecated
    public ReferenceTypeDeserializer(final JavaType fullType, final TypeDeserializer typeDeser, final JsonDeserializer<?> deser) {
        this(fullType, null, typeDeser, deser);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._valueDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property, this._fullType.getReferencedType());
        }
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return this.withResolved(typeDeser, deser);
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    protected abstract ReferenceTypeDeserializer<T> withResolved(final TypeDeserializer p0, final JsonDeserializer<?> p1);
    
    @Override
    public abstract T getNullValue(final DeserializationContext p0);
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) {
        return this.getNullValue(ctxt);
    }
    
    public abstract T referenceValue(final Object p0);
    
    public abstract T updateReference(final T p0, final Object p1);
    
    public abstract Object getReferenced(final T p0);
    
    @Override
    public JavaType getValueType() {
        return this._fullType;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return (this._valueDeserializer == null) ? null : this._valueDeserializer.supportsUpdate(config);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._valueInstantiator != null) {
            final T value = (T)this._valueInstantiator.createUsingDefault(ctxt);
            return this.deserialize(p, ctxt, value);
        }
        final Object contents = (this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        return this.referenceValue(contents);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt, final T reference) throws IOException {
        final Boolean B = this._valueDeserializer.supportsUpdate(ctxt.getConfig());
        Object contents;
        if (B.equals(Boolean.FALSE) || this._valueTypeDeserializer != null) {
            contents = ((this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer));
        }
        else {
            contents = this.getReferenced(reference);
            if (contents == null) {
                contents = ((this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer));
                return this.referenceValue(contents);
            }
            contents = this._valueDeserializer.deserialize(p, ctxt, contents);
        }
        return this.updateReference(reference, contents);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return this.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer == null) {
            return this.deserialize(p, ctxt);
        }
        return this.referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
    }
}
