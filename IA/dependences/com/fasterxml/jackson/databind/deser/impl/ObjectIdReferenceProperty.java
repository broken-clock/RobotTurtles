// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public class ObjectIdReferenceProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    private final SettableBeanProperty _forward;
    
    public ObjectIdReferenceProperty(final SettableBeanProperty forward, final ObjectIdInfo objectIdInfo) {
        super(forward);
        this._forward = forward;
        this._objectIdInfo = objectIdInfo;
    }
    
    public ObjectIdReferenceProperty(final ObjectIdReferenceProperty src, final JsonDeserializer<?> deser, final NullValueProvider nva) {
        super(src, deser, nva);
        this._forward = src._forward;
        this._objectIdInfo = src._objectIdInfo;
    }
    
    public ObjectIdReferenceProperty(final ObjectIdReferenceProperty src, final PropertyName newName) {
        super(src, newName);
        this._forward = src._forward;
        this._objectIdInfo = src._objectIdInfo;
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new ObjectIdReferenceProperty(this, newName);
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        return new ObjectIdReferenceProperty(this, deser, this._nullProvider);
    }
    
    @Override
    public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
        return new ObjectIdReferenceProperty(this, this._valueDeserializer, nva);
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        if (this._forward != null) {
            this._forward.fixAccess(config);
        }
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._forward.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._forward.getMember();
    }
    
    @Override
    public int getCreatorIndex() {
        return this._forward.getCreatorIndex();
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        this.deserializeSetAndReturn(p, ctxt, instance);
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        try {
            return this.setAndReturn(instance, this.deserialize(p, ctxt));
        }
        catch (UnresolvedForwardReference reference) {
            final boolean usingIdentityInfo = this._objectIdInfo != null || this._valueDeserializer.getObjectIdReader() != null;
            if (!usingIdentityInfo) {
                throw JsonMappingException.from(p, "Unresolved forward reference but no identity info", reference);
            }
            reference.getRoid().appendReferring(new PropertyReferring(this, reference, this._type.getRawClass(), instance));
            return null;
        }
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        this._forward.set(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        return this._forward.setAndReturn(instance, value);
    }
    
    public static final class PropertyReferring extends ReadableObjectId.Referring
    {
        private final ObjectIdReferenceProperty _parent;
        public final Object _pojo;
        
        public PropertyReferring(final ObjectIdReferenceProperty parent, final UnresolvedForwardReference ref, final Class<?> type, final Object ob) {
            super(ref, type);
            this._parent = parent;
            this._pojo = ob;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            if (!this.hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
            }
            this._parent.set(this._pojo, value);
        }
    }
}
