// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Set;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable, SchemaAware
{
    protected final AnnotatedMember _accessor;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final BeanProperty _property;
    protected final boolean _forceTypeInformation;
    
    public JsonValueSerializer(final AnnotatedMember accessor, final JsonSerializer<?> ser) {
        super(accessor.getType());
        this._accessor = accessor;
        this._valueSerializer = (JsonSerializer<Object>)ser;
        this._property = null;
        this._forceTypeInformation = true;
    }
    
    public JsonValueSerializer(final JsonValueSerializer src, final BeanProperty property, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        super(_notNullClass(src.handledType()));
        this._accessor = src._accessor;
        this._valueSerializer = (JsonSerializer<Object>)ser;
        this._property = property;
        this._forceTypeInformation = forceTypeInfo;
    }
    
    private static final Class<Object> _notNullClass(final Class<?> cls) {
        return (Class<Object>)((cls == null) ? Object.class : cls);
    }
    
    public JsonValueSerializer withResolved(final BeanProperty property, final JsonSerializer<?> ser, final boolean forceTypeInfo) {
        if (this._property == property && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation) {
            return this;
        }
        return new JsonValueSerializer(this, property, ser, forceTypeInfo);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser != null) {
            ser = provider.handlePrimaryContextualization(ser, property);
            return this.withResolved(property, ser, this._forceTypeInformation);
        }
        final JavaType t = this._accessor.getType();
        if (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || t.isFinal()) {
            ser = provider.findPrimaryPropertySerializer(t, property);
            final boolean forceTypeInformation = this.isNaturalTypeWithStdHandling(t.getRawClass(), ser);
            return this.withResolved(property, ser, forceTypeInformation);
        }
        return this;
    }
    
    @Override
    public void serialize(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws IOException {
        try {
            final Object value = this._accessor.getValue(bean);
            if (value == null) {
                prov.defaultSerializeNull(gen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                final Class<?> c = value.getClass();
                ser = prov.findTypedValueSerializer(c, true, this._property);
            }
            ser.serialize(value, gen, prov);
        }
        catch (Exception e) {
            this.wrapAndThrow(prov, e, bean, this._accessor.getName() + "()");
        }
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer0) throws IOException {
        Object value = null;
        try {
            value = this._accessor.getValue(bean);
            if (value == null) {
                provider.defaultSerializeNull(gen);
                return;
            }
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = provider.findValueSerializer(value.getClass(), this._property);
            }
            else if (this._forceTypeInformation) {
                final WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0.typeId(bean, JsonToken.VALUE_STRING));
                ser.serialize(value, gen, provider);
                typeSer0.writeTypeSuffix(gen, typeIdDef);
                return;
            }
            final TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
            ser.serializeWithType(value, gen, provider, rr);
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, bean, this._accessor.getName() + "()");
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        if (this._valueSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._valueSerializer).getSchema(provider, null);
        }
        return JsonSchema.getDefaultSchemaNode();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JavaType type = this._accessor.getType();
        final Class<?> declaring = this._accessor.getDeclaringClass();
        if (declaring != null && declaring.isEnum() && this._acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = visitor.getProvider().findTypedValueSerializer(type, false, this._property);
            if (ser == null) {
                visitor.expectAnyFormat(typeHint);
                return;
            }
        }
        ser.acceptJsonFormatVisitor(visitor, type);
    }
    
    protected boolean _acceptJsonFormatVisitorForEnum(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final Class<?> enumType) throws JsonMappingException {
        final JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
        if (stringVisitor != null) {
            final Set<String> enums = new LinkedHashSet<String>();
            for (final Object en : enumType.getEnumConstants()) {
                try {
                    enums.add(String.valueOf(this._accessor.getValue(en)));
                }
                catch (Exception e) {
                    Throwable t;
                    for (t = e; t instanceof InvocationTargetException && t.getCause() != null; t = t.getCause()) {}
                    ClassUtil.throwIfError(t);
                    throw JsonMappingException.wrapWithPath(t, en, this._accessor.getName() + "()");
                }
            }
            stringVisitor.enumTypes(enums);
        }
        return true;
    }
    
    protected boolean isNaturalTypeWithStdHandling(final Class<?> rawType, final JsonSerializer<?> ser) {
        if (rawType.isPrimitive()) {
            if (rawType != Integer.TYPE && rawType != Boolean.TYPE && rawType != Double.TYPE) {
                return false;
            }
        }
        else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
            return false;
        }
        return this.isDefaultSerializer(ser);
    }
    
    @Override
    public String toString() {
        return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
    }
    
    static class TypeSerializerRerouter extends TypeSerializer
    {
        protected final TypeSerializer _typeSerializer;
        protected final Object _forObject;
        
        public TypeSerializerRerouter(final TypeSerializer ts, final Object ob) {
            this._typeSerializer = ts;
            this._forObject = ob;
        }
        
        @Override
        public TypeSerializer forProperty(final BeanProperty prop) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public JsonTypeInfo.As getTypeInclusion() {
            return this._typeSerializer.getTypeInclusion();
        }
        
        @Override
        public String getPropertyName() {
            return this._typeSerializer.getPropertyName();
        }
        
        @Override
        public TypeIdResolver getTypeIdResolver() {
            return this._typeSerializer.getTypeIdResolver();
        }
        
        @Override
        public WritableTypeId writeTypePrefix(final JsonGenerator g, final WritableTypeId typeId) throws IOException {
            typeId.forValue = this._forObject;
            return this._typeSerializer.writeTypePrefix(g, typeId);
        }
        
        @Override
        public WritableTypeId writeTypeSuffix(final JsonGenerator g, final WritableTypeId typeId) throws IOException {
            return this._typeSerializer.writeTypeSuffix(g, typeId);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForScalar(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForObject(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForArray(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForScalar(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForObject(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypeSuffixForArray(final Object value, final JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForScalar(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForObject(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeTypePrefixForArray(final Object value, final JsonGenerator gen, final Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForScalar(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypePrefixForArray(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForScalar(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
        }
        
        @Deprecated
        @Override
        public void writeCustomTypeSuffixForArray(final Object value, final JsonGenerator gen, final String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
        }
    }
}
