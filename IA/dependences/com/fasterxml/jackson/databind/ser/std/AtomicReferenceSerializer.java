// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceSerializer extends ReferenceTypeSerializer<AtomicReference<?>>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicReferenceSerializer(final ReferenceType fullType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }
    
    protected AtomicReferenceSerializer(final AtomicReferenceSerializer base, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSer, final NameTransformer unwrapper, final Object suppressableValue, final boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper, suppressableValue, suppressNulls);
    }
    
    @Override
    protected ReferenceTypeSerializer<AtomicReference<?>> withResolved(final BeanProperty prop, final TypeSerializer vts, final JsonSerializer<?> valueSer, final NameTransformer unwrapper) {
        return new AtomicReferenceSerializer(this, prop, vts, valueSer, unwrapper, this._suppressableValue, this._suppressNulls);
    }
    
    @Override
    public ReferenceTypeSerializer<AtomicReference<?>> withContentInclusion(final Object suppressableValue, final boolean suppressNulls) {
        return new AtomicReferenceSerializer(this, this._property, this._valueTypeSerializer, this._valueSerializer, this._unwrapper, suppressableValue, suppressNulls);
    }
    
    @Override
    protected boolean _isValuePresent(final AtomicReference<?> value) {
        return value.get() != null;
    }
    
    @Override
    protected Object _getReferenced(final AtomicReference<?> value) {
        return value.get();
    }
    
    @Override
    protected Object _getReferencedIfPresent(final AtomicReference<?> value) {
        return value.get();
    }
}
