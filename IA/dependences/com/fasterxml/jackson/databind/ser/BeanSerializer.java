// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class BeanSerializer extends BeanSerializerBase
{
    private static final long serialVersionUID = 29L;
    
    public BeanSerializer(final JavaType type, final BeanSerializerBuilder builder, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(type, builder, properties, filteredProperties);
    }
    
    protected BeanSerializer(final BeanSerializerBase src) {
        super(src);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src, objectIdWriter, filterId);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final Set<String> toIgnore) {
        super(src, toIgnore);
    }
    
    public static BeanSerializer createDummy(final JavaType forType) {
        return new BeanSerializer(forType, null, BeanSerializer.NO_PROPS, null);
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }
    
    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new BeanSerializer(this, objectIdWriter, this._propertyFilterId);
    }
    
    @Override
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new BeanSerializer(this, this._objectIdWriter, filterId);
    }
    
    @Override
    protected BeanSerializerBase withIgnorals(final Set<String> toIgnore) {
        return new BeanSerializer(this, toIgnore);
    }
    
    @Override
    protected BeanSerializerBase asArraySerializer() {
        if (this._objectIdWriter == null && this._anyGetterWriter == null && this._propertyFilterId == null) {
            return new BeanAsArraySerializer(this);
        }
        return this;
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (this._objectIdWriter != null) {
            gen.setCurrentValue(bean);
            this._serializeWithObjectId(bean, gen, provider, true);
            return;
        }
        gen.writeStartObject(bean);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
        }
        else {
            this.serializeFields(bean, gen, provider);
        }
        gen.writeEndObject();
    }
    
    @Override
    public String toString() {
        return "BeanSerializer for " + this.handledType().getName();
    }
}
