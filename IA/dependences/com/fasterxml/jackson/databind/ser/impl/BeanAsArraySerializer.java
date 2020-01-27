// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class BeanAsArraySerializer extends BeanSerializerBase
{
    private static final long serialVersionUID = 1L;
    protected final BeanSerializerBase _defaultSerializer;
    
    public BeanAsArraySerializer(final BeanSerializerBase src) {
        super(src, (ObjectIdWriter)null);
        this._defaultSerializer = src;
    }
    
    protected BeanAsArraySerializer(final BeanSerializerBase src, final Set<String> toIgnore) {
        super(src, toIgnore);
        this._defaultSerializer = src;
    }
    
    protected BeanAsArraySerializer(final BeanSerializerBase src, final ObjectIdWriter oiw, final Object filterId) {
        super(src, oiw, filterId);
        this._defaultSerializer = src;
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer transformer) {
        return this._defaultSerializer.unwrappingSerializer(transformer);
    }
    
    @Override
    public boolean isUnwrappingSerializer() {
        return false;
    }
    
    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return this._defaultSerializer.withObjectIdWriter(objectIdWriter);
    }
    
    @Override
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new BeanAsArraySerializer(this, this._objectIdWriter, filterId);
    }
    
    @Override
    protected BeanAsArraySerializer withIgnorals(final Set<String> toIgnore) {
        return new BeanAsArraySerializer(this, toIgnore);
    }
    
    @Override
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        gen.setCurrentValue(bean);
        final WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_ARRAY);
        typeSer.writeTypePrefix(gen, typeIdDef);
        this.serializeAsArray(bean, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && this.hasSingleElement(provider)) {
            this.serializeAsArray(bean, gen, provider);
            return;
        }
        gen.writeStartArray();
        gen.setCurrentValue(bean);
        this.serializeAsArray(bean, gen, provider);
        gen.writeEndArray();
    }
    
    private boolean hasSingleElement(final SerializerProvider provider) {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        return props.length == 1;
    }
    
    protected final void serializeAsArray(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop == null) {
                    gen.writeNull();
                }
                else {
                    prop.serializeAsElement(bean, gen, provider);
                }
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = JsonMappingException.from(gen, "Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    @Override
    public String toString() {
        return "BeanAsArraySerializer for " + this.handledType().getName();
    }
}
