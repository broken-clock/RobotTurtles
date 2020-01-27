// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import java.util.IdentityHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.JsonSerializer;

public abstract class StdSerializer<T> extends JsonSerializer<T> implements JsonFormatVisitable, SchemaAware, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Object KEY_CONTENT_CONVERTER_LOCK;
    protected final Class<T> _handledType;
    
    protected StdSerializer(final Class<T> t) {
        this._handledType = t;
    }
    
    protected StdSerializer(final JavaType type) {
        this._handledType = (Class<T>)type.getRawClass();
    }
    
    protected StdSerializer(final Class<?> t, final boolean dummy) {
        this._handledType = (Class<T>)t;
    }
    
    protected StdSerializer(final StdSerializer<?> src) {
        this._handledType = (Class<T>)src._handledType;
    }
    
    @Override
    public Class<T> handledType() {
        return this._handledType;
    }
    
    @Override
    public abstract void serialize(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        visitor.expectAnyFormat(typeHint);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return this.createSchemaNode("string");
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint, final boolean isOptional) throws JsonMappingException {
        final ObjectNode schema = (ObjectNode)this.getSchema(provider, typeHint);
        if (!isOptional) {
            schema.put("required", !isOptional);
        }
        return schema;
    }
    
    protected ObjectNode createSchemaNode(final String type) {
        final ObjectNode schema = JsonNodeFactory.instance.objectNode();
        schema.put("type", type);
        return schema;
    }
    
    protected ObjectNode createSchemaNode(final String type, final boolean isOptional) {
        final ObjectNode schema = this.createSchemaNode(type);
        if (!isOptional) {
            schema.put("required", !isOptional);
        }
        return schema;
    }
    
    protected void visitStringFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        visitor.expectStringFormat(typeHint);
    }
    
    protected void visitStringFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonValueFormat format) throws JsonMappingException {
        final JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            v2.format(format);
        }
    }
    
    protected void visitIntFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonParser.NumberType numberType) throws JsonMappingException {
        final JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (_neitherNull(v2, numberType)) {
            v2.numberType(numberType);
        }
    }
    
    protected void visitIntFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonParser.NumberType numberType, final JsonValueFormat format) throws JsonMappingException {
        final JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            if (numberType != null) {
                v2.numberType(numberType);
            }
            if (format != null) {
                v2.format(format);
            }
        }
    }
    
    protected void visitFloatFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonParser.NumberType numberType) throws JsonMappingException {
        final JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
        if (v2 != null) {
            v2.numberType(numberType);
        }
    }
    
    protected void visitArrayFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonSerializer<?> itemSerializer, final JavaType itemType) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (_neitherNull(v2, itemSerializer)) {
            v2.itemsFormat(itemSerializer, itemType);
        }
    }
    
    protected void visitArrayFormat(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final JsonFormatTypes itemType) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(itemType);
        }
    }
    
    public void wrapAndThrow(final SerializerProvider provider, Throwable t, final Object bean, final String fieldName) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        final boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }
    
    public void wrapAndThrow(final SerializerProvider provider, Throwable t, final Object bean, final int index) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        final boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, index);
    }
    
    protected JsonSerializer<?> findContextualConvertingSerializer(final SerializerProvider provider, final BeanProperty property, final JsonSerializer<?> existingSerializer) throws JsonMappingException {
        Map<Object, Object> conversions = (Map<Object, Object>)provider.getAttribute(StdSerializer.KEY_CONTENT_CONVERTER_LOCK);
        if (conversions != null) {
            final Object lock = conversions.get(property);
            if (lock != null) {
                return existingSerializer;
            }
        }
        else {
            conversions = new IdentityHashMap<Object, Object>();
            provider.setAttribute(StdSerializer.KEY_CONTENT_CONVERTER_LOCK, conversions);
        }
        conversions.put(property, Boolean.TRUE);
        try {
            final JsonSerializer<?> ser = this.findConvertingContentSerializer(provider, property, existingSerializer);
            if (ser != null) {
                return provider.handleSecondaryContextualization(ser, property);
            }
        }
        finally {
            conversions.remove(property);
        }
        return existingSerializer;
    }
    
    @Deprecated
    protected JsonSerializer<?> findConvertingContentSerializer(final SerializerProvider provider, final BeanProperty prop, JsonSerializer<?> existingSerializer) throws JsonMappingException {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (_neitherNull(intr, prop)) {
            final AnnotatedMember m = prop.getMember();
            if (m != null) {
                final Object convDef = intr.findSerializationContentConverter(m);
                if (convDef != null) {
                    final Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
                    final JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
                    if (existingSerializer == null && !delegateType.isJavaLangObject()) {
                        existingSerializer = provider.findValueSerializer(delegateType);
                    }
                    return new StdDelegatingSerializer(conv, delegateType, existingSerializer);
                }
            }
        }
        return existingSerializer;
    }
    
    protected PropertyFilter findPropertyFilter(final SerializerProvider provider, final Object filterId, final Object valueToFilter) throws JsonMappingException {
        final FilterProvider filters = provider.getFilterProvider();
        if (filters == null) {
            provider.reportBadDefinition(this.handledType(), "Cannot resolve PropertyFilter with id '" + filterId + "'; no FilterProvider configured");
        }
        return filters.findPropertyFilter(filterId, valueToFilter);
    }
    
    protected JsonFormat.Value findFormatOverrides(final SerializerProvider provider, final BeanProperty prop, final Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyFormat(typeForDefaults);
    }
    
    protected Boolean findFormatFeature(final SerializerProvider provider, final BeanProperty prop, final Class<?> typeForDefaults, final JsonFormat.Feature feat) {
        final JsonFormat.Value format = this.findFormatOverrides(provider, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }
    
    protected JsonInclude.Value findIncludeOverrides(final SerializerProvider provider, final BeanProperty prop, final Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyInclusion(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyInclusion(typeForDefaults);
    }
    
    protected JsonSerializer<?> findAnnotatedContentSerializer(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        if (property != null) {
            final AnnotatedMember m = property.getMember();
            final AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            if (m != null) {
                final Object serDef = intr.findContentSerializer(m);
                if (serDef != null) {
                    return serializers.serializerInstance(m, serDef);
                }
            }
        }
        return null;
    }
    
    protected boolean isDefaultSerializer(final JsonSerializer<?> serializer) {
        return ClassUtil.isJacksonStdImpl(serializer);
    }
    
    protected static final boolean _neitherNull(final Object a, final Object b) {
        return a != null && b != null;
    }
    
    protected static final boolean _nonEmpty(final Collection<?> c) {
        return c != null && !c.isEmpty();
    }
    
    static {
        KEY_CONTENT_CONVERTER_LOCK = new Object();
    }
}
