// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import java.util.Calendar;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javax.xml.datatype.XMLGregorianCalendar;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.xml.namespace.QName;
import javax.xml.datatype.Duration;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;

public class CoreXMLSerializers extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
        final Class<?> raw = type.getRawClass();
        if (Duration.class.isAssignableFrom(raw) || QName.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (XMLGregorianCalendar.class.isAssignableFrom(raw)) {
            return XMLGregorianCalendarSerializer.instance;
        }
        return null;
    }
    
    public static class XMLGregorianCalendarSerializer extends StdSerializer<XMLGregorianCalendar> implements ContextualSerializer
    {
        static final XMLGregorianCalendarSerializer instance;
        final JsonSerializer<Object> _delegate;
        
        public XMLGregorianCalendarSerializer() {
            this((JsonSerializer<?>)CalendarSerializer.instance);
        }
        
        protected XMLGregorianCalendarSerializer(final JsonSerializer<?> del) {
            super(XMLGregorianCalendar.class);
            this._delegate = (JsonSerializer<Object>)del;
        }
        
        @Override
        public JsonSerializer<?> getDelegatee() {
            return this._delegate;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider provider, final XMLGregorianCalendar value) {
            return this._delegate.isEmpty(provider, this._convert(value));
        }
        
        @Override
        public void serialize(final XMLGregorianCalendar value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            this._delegate.serialize(this._convert(value), gen, provider);
        }
        
        @Override
        public void serializeWithType(final XMLGregorianCalendar value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            this._delegate.serializeWithType(this._convert(value), gen, provider, typeSer);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this._delegate.acceptJsonFormatVisitor(visitor, null);
        }
        
        @Override
        public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
            final JsonSerializer<?> ser = prov.handlePrimaryContextualization(this._delegate, property);
            if (ser != this._delegate) {
                return new XMLGregorianCalendarSerializer(ser);
            }
            return this;
        }
        
        protected Calendar _convert(final XMLGregorianCalendar input) {
            return (input == null) ? null : input.toGregorianCalendar();
        }
        
        static {
            instance = new XMLGregorianCalendarSerializer();
        }
    }
}
