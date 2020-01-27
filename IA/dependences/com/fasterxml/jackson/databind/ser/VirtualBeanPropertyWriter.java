// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.io.Serializable;

public abstract class VirtualBeanPropertyWriter extends BeanPropertyWriter implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected VirtualBeanPropertyWriter(final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType) {
        this(propDef, contextAnnotations, declaredType, null, null, null, propDef.findInclusion());
    }
    
    protected VirtualBeanPropertyWriter() {
    }
    
    protected VirtualBeanPropertyWriter(final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final JavaType serType, final JsonInclude.Value inclusion, final Class<?>[] includeInViews) {
        super(propDef, propDef.getPrimaryMember(), contextAnnotations, declaredType, ser, typeSer, serType, _suppressNulls(inclusion), _suppressableValue(inclusion), includeInViews);
    }
    
    @Deprecated
    protected VirtualBeanPropertyWriter(final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final JavaType serType, final JsonInclude.Value inclusion) {
        this(propDef, contextAnnotations, declaredType, ser, typeSer, serType, inclusion, null);
    }
    
    protected VirtualBeanPropertyWriter(final VirtualBeanPropertyWriter base) {
        super(base);
    }
    
    protected VirtualBeanPropertyWriter(final VirtualBeanPropertyWriter base, final PropertyName name) {
        super(base, name);
    }
    
    protected static boolean _suppressNulls(final JsonInclude.Value inclusion) {
        if (inclusion == null) {
            return false;
        }
        final JsonInclude.Include incl = inclusion.getValueInclusion();
        return incl != JsonInclude.Include.ALWAYS && incl != JsonInclude.Include.USE_DEFAULTS;
    }
    
    protected static Object _suppressableValue(final JsonInclude.Value inclusion) {
        if (inclusion == null) {
            return false;
        }
        final JsonInclude.Include incl = inclusion.getValueInclusion();
        if (incl == JsonInclude.Include.ALWAYS || incl == JsonInclude.Include.NON_NULL || incl == JsonInclude.Include.USE_DEFAULTS) {
            return null;
        }
        return VirtualBeanPropertyWriter.MARKER_FOR_EMPTY;
    }
    
    @Override
    public boolean isVirtual() {
        return true;
    }
    
    protected abstract Object value(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract VirtualBeanPropertyWriter withConfig(final MapperConfig<?> p0, final AnnotatedClass p1, final BeanPropertyDefinition p2, final JavaType p3);
    
    @Override
    public void serializeAsField(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
        final Object value = this.value(bean, gen, prov);
        if (value == null) {
            if (this._nullSerializer != null) {
                gen.writeFieldName(this._name);
                this._nullSerializer.serialize(null, gen, prov);
            }
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap m = this._dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(m, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (VirtualBeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        gen.writeFieldName(this._name);
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        }
        else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsElement(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
        final Object value = this.value(bean, gen, prov);
        if (value == null) {
            if (this._nullSerializer != null) {
                this._nullSerializer.serialize(null, gen, prov);
            }
            else {
                gen.writeNull();
            }
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (VirtualBeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    this.serializeAsPlaceholder(bean, gen, prov);
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                this.serializeAsPlaceholder(bean, gen, prov);
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        }
        else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }
}
