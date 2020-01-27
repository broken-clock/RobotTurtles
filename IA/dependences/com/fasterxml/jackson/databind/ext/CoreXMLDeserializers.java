// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.Date;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import javax.xml.datatype.DatatypeFactory;
import com.fasterxml.jackson.databind.deser.Deserializers;

public class CoreXMLDeserializers extends Deserializers.Base
{
    static final DatatypeFactory _dataTypeFactory;
    protected static final int TYPE_DURATION = 1;
    protected static final int TYPE_G_CALENDAR = 2;
    protected static final int TYPE_QNAME = 3;
    
    @Override
    public JsonDeserializer<?> findBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) {
        final Class<?> raw = type.getRawClass();
        if (raw == QName.class) {
            return new Std(raw, 3);
        }
        if (raw == XMLGregorianCalendar.class) {
            return new Std(raw, 2);
        }
        if (raw == Duration.class) {
            return new Std(raw, 1);
        }
        return null;
    }
    
    static {
        try {
            _dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class Std extends FromStringDeserializer<Object>
    {
        private static final long serialVersionUID = 1L;
        protected final int _kind;
        
        public Std(final Class<?> raw, final int kind) {
            super(raw);
            this._kind = kind;
        }
        
        @Override
        public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (this._kind == 2 && p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return this._gregorianFromDate(ctxt, this._parseDate(p, ctxt));
            }
            return super.deserialize(p, ctxt);
        }
        
        @Override
        protected Object _deserialize(final String value, final DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1: {
                    return CoreXMLDeserializers._dataTypeFactory.newDuration(value);
                }
                case 3: {
                    return QName.valueOf(value);
                }
                case 2: {
                    Date d;
                    try {
                        d = this._parseDate(value, ctxt);
                    }
                    catch (JsonMappingException e) {
                        return CoreXMLDeserializers._dataTypeFactory.newXMLGregorianCalendar(value);
                    }
                    return this._gregorianFromDate(ctxt, d);
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        protected XMLGregorianCalendar _gregorianFromDate(final DeserializationContext ctxt, final Date d) {
            if (d == null) {
                return null;
            }
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            final TimeZone tz = ctxt.getTimeZone();
            if (tz != null) {
                calendar.setTimeZone(tz);
            }
            return CoreXMLDeserializers._dataTypeFactory.newXMLGregorianCalendar(calendar);
        }
    }
}
