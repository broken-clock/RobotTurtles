// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class ManagedReferenceProperty extends Delegating
{
    private static final long serialVersionUID = 1L;
    protected final String _referenceName;
    protected final boolean _isContainer;
    protected final SettableBeanProperty _backProperty;
    
    public ManagedReferenceProperty(final SettableBeanProperty forward, final String refName, final SettableBeanProperty backward, final boolean isContainer) {
        super(forward);
        this._referenceName = refName;
        this._backProperty = backward;
        this._isContainer = isContainer;
    }
    
    @Override
    protected SettableBeanProperty withDelegate(final SettableBeanProperty d) {
        throw new IllegalStateException("Should never try to reset delegate");
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        this.delegate.fixAccess(config);
        this._backProperty.fixAccess(config);
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        this.set(instance, this.delegate.deserialize(p, ctxt));
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        return this.setAndReturn(instance, this.deserialize(p, ctxt));
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        this.setAndReturn(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        if (value != null) {
            if (this._isContainer) {
                if (value instanceof Object[]) {
                    for (final Object ob : (Object[])value) {
                        if (ob != null) {
                            this._backProperty.set(ob, instance);
                        }
                    }
                }
                else if (value instanceof Collection) {
                    for (final Object ob2 : (Collection)value) {
                        if (ob2 != null) {
                            this._backProperty.set(ob2, instance);
                        }
                    }
                }
                else {
                    if (!(value instanceof Map)) {
                        throw new IllegalStateException("Unsupported container type (" + value.getClass().getName() + ") when resolving reference '" + this._referenceName + "'");
                    }
                    for (final Object ob2 : ((Map)value).values()) {
                        if (ob2 != null) {
                            this._backProperty.set(ob2, instance);
                        }
                    }
                }
            }
            else {
                this._backProperty.set(value, instance);
            }
        }
        return this.delegate.setAndReturn(instance, value);
    }
}
