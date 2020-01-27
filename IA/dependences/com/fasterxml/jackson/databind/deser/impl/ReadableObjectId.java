// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Collections;
import java.io.IOException;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import java.util.LinkedList;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;

public class ReadableObjectId
{
    protected Object _item;
    protected final ObjectIdGenerator.IdKey _key;
    protected LinkedList<Referring> _referringProperties;
    protected ObjectIdResolver _resolver;
    
    public ReadableObjectId(final ObjectIdGenerator.IdKey key) {
        this._key = key;
    }
    
    public void setResolver(final ObjectIdResolver resolver) {
        this._resolver = resolver;
    }
    
    public ObjectIdGenerator.IdKey getKey() {
        return this._key;
    }
    
    public void appendReferring(final Referring currentReferring) {
        if (this._referringProperties == null) {
            this._referringProperties = new LinkedList<Referring>();
        }
        this._referringProperties.add(currentReferring);
    }
    
    public void bindItem(final Object ob) throws IOException {
        this._resolver.bindItem(this._key, ob);
        this._item = ob;
        final Object id = this._key.key;
        if (this._referringProperties != null) {
            final Iterator<Referring> it = this._referringProperties.iterator();
            this._referringProperties = null;
            while (it.hasNext()) {
                it.next().handleResolvedForwardReference(id, ob);
            }
        }
    }
    
    public Object resolve() {
        return this._item = this._resolver.resolveId(this._key);
    }
    
    public boolean hasReferringProperties() {
        return this._referringProperties != null && !this._referringProperties.isEmpty();
    }
    
    public Iterator<Referring> referringProperties() {
        if (this._referringProperties == null) {
            return Collections.emptyList().iterator();
        }
        return this._referringProperties.iterator();
    }
    
    public boolean tryToResolveUnresolved(final DeserializationContext ctxt) {
        return false;
    }
    
    public ObjectIdResolver getResolver() {
        return this._resolver;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this._key);
    }
    
    public abstract static class Referring
    {
        private final UnresolvedForwardReference _reference;
        private final Class<?> _beanType;
        
        public Referring(final UnresolvedForwardReference ref, final Class<?> beanType) {
            this._reference = ref;
            this._beanType = beanType;
        }
        
        public Referring(final UnresolvedForwardReference ref, final JavaType beanType) {
            this._reference = ref;
            this._beanType = beanType.getRawClass();
        }
        
        public JsonLocation getLocation() {
            return this._reference.getLocation();
        }
        
        public Class<?> getBeanType() {
            return this._beanType;
        }
        
        public abstract void handleResolvedForwardReference(final Object p0, final Object p1) throws IOException;
        
        public boolean hasId(final Object id) {
            return id.equals(this._reference.getUnresolvedId());
        }
    }
}
