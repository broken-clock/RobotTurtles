// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.Collection;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;

public class ArrayBlockingQueueDeserializer extends CollectionDeserializer
{
    private static final long serialVersionUID = 1L;
    
    public ArrayBlockingQueueDeserializer(final JavaType containerType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator) {
        super(containerType, valueDeser, valueTypeDeser, valueInstantiator);
    }
    
    protected ArrayBlockingQueueDeserializer(final JavaType containerType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator, final JsonDeserializer<Object> delegateDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(containerType, valueDeser, valueTypeDeser, valueInstantiator, delegateDeser, nuller, unwrapSingle);
    }
    
    protected ArrayBlockingQueueDeserializer(final ArrayBlockingQueueDeserializer src) {
        super(src);
    }
    
    @Override
    protected ArrayBlockingQueueDeserializer withResolved(final JsonDeserializer<?> dd, final JsonDeserializer<?> vd, final TypeDeserializer vtd, final NullValueProvider nuller, final Boolean unwrapSingle) {
        return new ArrayBlockingQueueDeserializer(this._containerType, (JsonDeserializer<Object>)vd, vtd, this._valueInstantiator, (JsonDeserializer<Object>)dd, nuller, unwrapSingle);
    }
    
    @Override
    protected Collection<Object> createDefaultInstance(final DeserializationContext ctxt) throws IOException {
        return null;
    }
    
    @Override
    public Collection<Object> deserialize(final JsonParser p, final DeserializationContext ctxt, Collection<Object> result0) throws IOException {
        if (result0 != null) {
            return super.deserialize(p, ctxt, result0);
        }
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, new ArrayBlockingQueue<Object>(1));
        }
        result0 = super.deserialize(p, ctxt, new ArrayList<Object>());
        return new ArrayBlockingQueue<Object>(result0.size(), false, result0);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
}
