// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import java.io.IOException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import java.lang.reflect.Constructor;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class InnerClassProperty extends Delegating
{
    private static final long serialVersionUID = 1L;
    protected final transient Constructor<?> _creator;
    protected AnnotatedConstructor _annotated;
    
    public InnerClassProperty(final SettableBeanProperty delegate, final Constructor<?> ctor) {
        super(delegate);
        this._creator = ctor;
    }
    
    protected InnerClassProperty(final SettableBeanProperty src, final AnnotatedConstructor ann) {
        super(src);
        this._annotated = ann;
        this._creator = ((this._annotated == null) ? null : this._annotated.getAnnotated());
        if (this._creator == null) {
            throw new IllegalArgumentException("Missing constructor (broken JDK (de)serialization?)");
        }
    }
    
    @Override
    protected SettableBeanProperty withDelegate(final SettableBeanProperty d) {
        if (d == this.delegate) {
            return this;
        }
        return new InnerClassProperty(d, this._creator);
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        final JsonToken t = p.getCurrentToken();
        Object value;
        if (t == JsonToken.VALUE_NULL) {
            value = this._valueDeserializer.getNullValue(ctxt);
        }
        else if (this._valueTypeDeserializer != null) {
            value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        else {
            try {
                value = this._creator.newInstance(bean);
            }
            catch (Exception e) {
                ClassUtil.unwrapAndThrowAsIAE(e, String.format("Failed to instantiate class %s, problem: %s", this._creator.getDeclaringClass().getName(), e.getMessage()));
                value = null;
            }
            this._valueDeserializer.deserialize(p, ctxt, value);
        }
        this.set(bean, value);
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        return this.setAndReturn(instance, this.deserialize(p, ctxt));
    }
    
    Object readResolve() {
        return new InnerClassProperty(this, this._annotated);
    }
    
    Object writeReplace() {
        if (this._annotated == null) {
            return new InnerClassProperty(this, new AnnotatedConstructor(null, this._creator, null, null));
        }
        return this;
    }
}
