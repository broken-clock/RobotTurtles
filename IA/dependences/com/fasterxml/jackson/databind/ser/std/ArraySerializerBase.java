// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

public abstract class ArraySerializerBase<T> extends ContainerSerializer<T> implements ContextualSerializer
{
    protected final BeanProperty _property;
    protected final Boolean _unwrapSingle;
    
    protected ArraySerializerBase(final Class<T> cls) {
        super(cls);
        this._property = null;
        this._unwrapSingle = null;
    }
    
    @Deprecated
    protected ArraySerializerBase(final Class<T> cls, final BeanProperty property) {
        super(cls);
        this._property = property;
        this._unwrapSingle = null;
    }
    
    protected ArraySerializerBase(final ArraySerializerBase<?> src) {
        super(src._handledType, false);
        this._property = src._property;
        this._unwrapSingle = src._unwrapSingle;
    }
    
    protected ArraySerializerBase(final ArraySerializerBase<?> src, final BeanProperty property, final Boolean unwrapSingle) {
        super(src._handledType, false);
        this._property = property;
        this._unwrapSingle = unwrapSingle;
    }
    
    @Deprecated
    protected ArraySerializerBase(final ArraySerializerBase<?> src, final BeanProperty property) {
        super(src._handledType, false);
        this._property = property;
        this._unwrapSingle = src._unwrapSingle;
    }
    
    public abstract JsonSerializer<?> _withResolved(final BeanProperty p0, final Boolean p1);
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        Boolean unwrapSingle = null;
        if (property != null) {
            final JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
            if (format != null) {
                unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
                if (unwrapSingle != this._unwrapSingle) {
                    return this._withResolved(property, unwrapSingle);
                }
            }
        }
        return this;
    }
    
    @Override
    public void serialize(final T value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (this._shouldUnwrapSingle(provider) && this.hasSingleElement(value)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.setCurrentValue(value);
        gen.writeStartArray();
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    @Override
    public final void serializeWithType(final T value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        g.setCurrentValue(value);
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        this.serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    protected abstract void serializeContents(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    protected final boolean _shouldUnwrapSingle(final SerializerProvider provider) {
        if (this._unwrapSingle == null) {
            return provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        return this._unwrapSingle;
    }
}
