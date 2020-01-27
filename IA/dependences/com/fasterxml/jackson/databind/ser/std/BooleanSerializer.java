// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public final class BooleanSerializer extends StdScalarSerializer<Object> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;
    protected final boolean _forPrimitive;
    
    public BooleanSerializer(final boolean forPrimitive) {
        super(forPrimitive ? Boolean.TYPE : Boolean.class, false);
        this._forPrimitive = forPrimitive;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = this.findFormatOverrides(serializers, property, Boolean.class);
        if (format != null) {
            final JsonFormat.Shape shape = format.getShape();
            if (shape.isNumeric()) {
                return new AsNumber(this._forPrimitive);
            }
        }
        return this;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.writeBoolean(Boolean.TRUE.equals(value));
    }
    
    @Override
    public final void serializeWithType(final Object value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        g.writeBoolean(Boolean.TRUE.equals(value));
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("boolean", !this._forPrimitive);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        visitor.expectBooleanFormat(typeHint);
    }
    
    static final class AsNumber extends StdScalarSerializer<Object> implements ContextualSerializer
    {
        private static final long serialVersionUID = 1L;
        protected final boolean _forPrimitive;
        
        public AsNumber(final boolean forPrimitive) {
            super(forPrimitive ? Boolean.TYPE : Boolean.class, false);
            this._forPrimitive = forPrimitive;
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            g.writeNumber(Boolean.FALSE.equals(value) ? 0 : 1);
        }
        
        @Override
        public final void serializeWithType(final Object value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            g.writeBoolean(Boolean.TRUE.equals(value));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
        }
        
        @Override
        public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
            final JsonFormat.Value format = this.findFormatOverrides(serializers, property, Boolean.class);
            if (format != null) {
                final JsonFormat.Shape shape = format.getShape();
                if (!shape.isNumeric()) {
                    return new BooleanSerializer(this._forPrimitive);
                }
            }
            return this;
        }
    }
}
