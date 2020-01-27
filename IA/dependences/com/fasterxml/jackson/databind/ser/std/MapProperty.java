// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

public class MapProperty extends PropertyWriter
{
    private static final long serialVersionUID = 1L;
    private static final BeanProperty BOGUS_PROP;
    protected final TypeSerializer _typeSerializer;
    protected final BeanProperty _property;
    protected Object _key;
    protected Object _value;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    
    public MapProperty(final TypeSerializer typeSer, final BeanProperty prop) {
        super((prop == null) ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : prop.getMetadata());
        this._typeSerializer = typeSer;
        this._property = ((prop == null) ? MapProperty.BOGUS_PROP : prop);
    }
    
    public void reset(final Object key, final Object value, final JsonSerializer<Object> keySer, final JsonSerializer<Object> valueSer) {
        this._key = key;
        this._value = value;
        this._keySerializer = keySer;
        this._valueSerializer = valueSer;
    }
    
    @Deprecated
    public void reset(final Object key, final JsonSerializer<Object> keySer, final JsonSerializer<Object> valueSer) {
        this.reset(key, this._value, keySer, valueSer);
    }
    
    @Override
    public String getName() {
        if (this._key instanceof String) {
            return (String)this._key;
        }
        return String.valueOf(this._key);
    }
    
    public Object getValue() {
        return this._value;
    }
    
    public void setValue(final Object v) {
        this._value = v;
    }
    
    @Override
    public PropertyName getFullName() {
        return new PropertyName(this.getName());
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._property.getAnnotation(acls);
    }
    
    @Override
    public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
        return this._property.getContextAnnotation(acls);
    }
    
    @Override
    public void serializeAsField(final Object map, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        this._keySerializer.serialize(this._key, gen, provider);
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(this._value, gen, provider);
        }
        else {
            this._valueSerializer.serializeWithType(this._value, gen, provider, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsOmittedField(final Object map, final JsonGenerator gen, final SerializerProvider provider) throws Exception {
        if (!gen.canOmitFields()) {
            gen.writeOmittedField(this.getName());
        }
    }
    
    @Override
    public void serializeAsElement(final Object map, final JsonGenerator gen, final SerializerProvider provider) throws Exception {
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(this._value, gen, provider);
        }
        else {
            this._valueSerializer.serializeWithType(this._value, gen, provider, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsPlaceholder(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws Exception {
        gen.writeNull();
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
        this._property.depositSchemaProperty(objectVisitor, provider);
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
    }
    
    @Override
    public JavaType getType() {
        return this._property.getType();
    }
    
    @Override
    public PropertyName getWrapperName() {
        return this._property.getWrapperName();
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._property.getMember();
    }
    
    static {
        BOGUS_PROP = new BeanProperty.Bogus();
    }
}
