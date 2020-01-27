// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import java.io.Closeable;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import java.util.Map;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.BeanProperty;
import java.io.Serializable;

public class SettableAnyProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final BeanProperty _property;
    protected final AnnotatedMember _setter;
    final boolean _setterIsField;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final KeyDeserializer _keyDeserializer;
    
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMember setter, final JavaType type, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer typeDeser) {
        this._property = property;
        this._setter = setter;
        this._type = type;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = typeDeser;
        this._keyDeserializer = keyDeser;
        this._setterIsField = (setter instanceof AnnotatedField);
    }
    
    @Deprecated
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMember setter, final JavaType type, final JsonDeserializer<Object> valueDeser, final TypeDeserializer typeDeser) {
        this(property, setter, type, null, valueDeser, typeDeser);
    }
    
    public SettableAnyProperty withValueDeserializer(final JsonDeserializer<Object> deser) {
        return new SettableAnyProperty(this._property, this._setter, this._type, this._keyDeserializer, deser, this._valueTypeDeserializer);
    }
    
    public void fixAccess(final DeserializationConfig config) {
        this._setter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    
    Object readResolve() {
        if (this._setter == null || this._setter.getAnnotated() == null) {
            throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
        }
        return this;
    }
    
    public BeanProperty getProperty() {
        return this._property;
    }
    
    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public final void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance, final String propName) throws IOException {
        try {
            final Object key = (this._keyDeserializer == null) ? propName : this._keyDeserializer.deserializeKey(propName, ctxt);
            this.set(instance, key, this.deserialize(p, ctxt));
        }
        catch (UnresolvedForwardReference reference) {
            if (this._valueDeserializer.getObjectIdReader() == null) {
                throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
            }
            final AnySetterReferring referring = new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName);
            reference.getRoid().appendReferring(referring);
        }
    }
    
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return this._valueDeserializer.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(p, ctxt);
    }
    
    public void set(final Object instance, final Object propName, final Object value) throws IOException {
        try {
            if (this._setterIsField) {
                final AnnotatedField field = (AnnotatedField)this._setter;
                final Map<Object, Object> val = (Map<Object, Object>)field.getValue(instance);
                if (val != null) {
                    val.put(propName, value);
                }
            }
            else {
                ((AnnotatedMethod)this._setter).callOnWith(instance, propName, value);
            }
        }
        catch (Exception e) {
            this._throwAsIOE(e, propName, value);
        }
    }
    
    protected void _throwAsIOE(final Exception e, final Object propName, final Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            final String actType = ClassUtil.classNameOf(value);
            final StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
            msg.append("' of class " + this.getClassName() + " (expected type: ").append(this._type);
            msg.append("; actual type: ").append(actType).append(")");
            final String origMsg = ClassUtil.exceptionMessage(e);
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            }
            else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(null, msg.toString(), e);
        }
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        final Throwable t = ClassUtil.getRootCause(e);
        throw new JsonMappingException(null, ClassUtil.exceptionMessage(t), t);
    }
    
    private String getClassName() {
        return this._setter.getDeclaringClass().getName();
    }
    
    @Override
    public String toString() {
        return "[any property on class " + this.getClassName() + "]";
    }
    
    private static class AnySetterReferring extends ReadableObjectId.Referring
    {
        private final SettableAnyProperty _parent;
        private final Object _pojo;
        private final String _propName;
        
        public AnySetterReferring(final SettableAnyProperty parent, final UnresolvedForwardReference reference, final Class<?> type, final Object instance, final String propName) {
            super(reference, type);
            this._parent = parent;
            this._pojo = instance;
            this._propName = propName;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            if (!this.hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
            }
            this._parent.set(this._pojo, this._propName, value);
        }
    }
}
