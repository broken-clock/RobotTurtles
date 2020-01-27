// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.util.Set;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.util.Iterator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.util.EnumValues;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class EnumSerializer extends StdScalarSerializer<Enum<?>> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;
    protected final EnumValues _values;
    protected final Boolean _serializeAsIndex;
    
    public EnumSerializer(final EnumValues v, final Boolean serializeAsIndex) {
        super(v.getEnumClass(), false);
        this._values = v;
        this._serializeAsIndex = serializeAsIndex;
    }
    
    public static EnumSerializer construct(final Class<?> enumClass, final SerializationConfig config, final BeanDescription beanDesc, final JsonFormat.Value format) {
        final EnumValues v = EnumValues.constructFromName(config, (Class<Enum<?>>)enumClass);
        final Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, null);
        return new EnumSerializer(v, serializeAsIndex);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
        if (format != null) {
            final Class<?> type = this.handledType();
            final Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, this._serializeAsIndex);
            if (serializeAsIndex != this._serializeAsIndex) {
                return new EnumSerializer(this._values, serializeAsIndex);
            }
        }
        return this;
    }
    
    public EnumValues getEnumValues() {
        return this._values;
    }
    
    @Override
    public final void serialize(final Enum<?> en, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (this._serializeAsIndex(serializers)) {
            gen.writeNumber(en.ordinal());
            return;
        }
        if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            gen.writeString(en.toString());
            return;
        }
        gen.writeString(this._values.serializedValueFor(en));
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        if (this._serializeAsIndex(provider)) {
            return this.createSchemaNode("integer", true);
        }
        final ObjectNode objectNode = this.createSchemaNode("string", true);
        if (typeHint != null) {
            final JavaType type = provider.constructType(typeHint);
            if (type.isEnumType()) {
                final ArrayNode enumNode = objectNode.putArray("enum");
                for (final SerializableString value : this._values.values()) {
                    enumNode.add(value.getValue());
                }
            }
        }
        return objectNode;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final SerializerProvider serializers = visitor.getProvider();
        if (this._serializeAsIndex(serializers)) {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
            return;
        }
        final JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
        if (stringVisitor != null) {
            final Set<String> enums = new LinkedHashSet<String>();
            if (serializers != null && serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
                for (final Enum<?> e : this._values.enums()) {
                    enums.add(e.toString());
                }
            }
            else {
                for (final SerializableString value : this._values.values()) {
                    enums.add(value.getValue());
                }
            }
            stringVisitor.enumTypes(enums);
        }
    }
    
    protected final boolean _serializeAsIndex(final SerializerProvider serializers) {
        if (this._serializeAsIndex != null) {
            return this._serializeAsIndex;
        }
        return serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }
    
    protected static Boolean _isShapeWrittenUsingIndex(final Class<?> enumClass, final JsonFormat.Value format, final boolean fromClass, final Boolean defaultValue) {
        final JsonFormat.Shape shape = (format == null) ? null : format.getShape();
        if (shape == null) {
            return defaultValue;
        }
        if (shape == JsonFormat.Shape.ANY || shape == JsonFormat.Shape.SCALAR) {
            return defaultValue;
        }
        if (shape == JsonFormat.Shape.STRING || shape == JsonFormat.Shape.NATURAL) {
            return Boolean.FALSE;
        }
        if (shape.isNumeric() || shape == JsonFormat.Shape.ARRAY) {
            return Boolean.TRUE;
        }
        throw new IllegalArgumentException(String.format("Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", shape, enumClass.getName(), fromClass ? "class" : "property"));
    }
}
