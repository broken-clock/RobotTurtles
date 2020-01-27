// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.JavaType;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDeserializer extends ReferenceTypeDeserializer<AtomicReference<Object>>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicReferenceDeserializer(final JavaType fullType, final ValueInstantiator inst, final TypeDeserializer typeDeser, final JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }
    
    public AtomicReferenceDeserializer withResolved(final TypeDeserializer typeDeser, final JsonDeserializer<?> valueDeser) {
        return new AtomicReferenceDeserializer(this._fullType, this._valueInstantiator, typeDeser, valueDeser);
    }
    
    @Override
    public AtomicReference<Object> getNullValue(final DeserializationContext ctxt) {
        return new AtomicReference<Object>();
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) {
        return new AtomicReference();
    }
    
    @Override
    public AtomicReference<Object> referenceValue(final Object contents) {
        return new AtomicReference<Object>(contents);
    }
    
    @Override
    public Object getReferenced(final AtomicReference<Object> reference) {
        return reference.get();
    }
    
    @Override
    public AtomicReference<Object> updateReference(final AtomicReference<Object> reference, final Object contents) {
        reference.set(contents);
        return reference;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
}
