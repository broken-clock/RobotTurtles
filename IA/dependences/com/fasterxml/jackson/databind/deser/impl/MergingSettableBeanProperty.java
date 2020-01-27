// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public class MergingSettableBeanProperty extends Delegating
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedMember _accessor;
    
    protected MergingSettableBeanProperty(final SettableBeanProperty delegate, final AnnotatedMember accessor) {
        super(delegate);
        this._accessor = accessor;
    }
    
    protected MergingSettableBeanProperty(final MergingSettableBeanProperty src, final SettableBeanProperty delegate) {
        super(delegate);
        this._accessor = src._accessor;
    }
    
    public static MergingSettableBeanProperty construct(final SettableBeanProperty delegate, final AnnotatedMember accessor) {
        return new MergingSettableBeanProperty(delegate, accessor);
    }
    
    @Override
    protected SettableBeanProperty withDelegate(final SettableBeanProperty d) {
        return new MergingSettableBeanProperty(d, this._accessor);
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        final Object oldValue = this._accessor.getValue(instance);
        Object newValue;
        if (oldValue == null) {
            newValue = this.delegate.deserialize(p, ctxt);
        }
        else {
            newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
        }
        if (newValue != oldValue) {
            this.delegate.set(instance, newValue);
        }
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        final Object oldValue = this._accessor.getValue(instance);
        Object newValue;
        if (oldValue == null) {
            newValue = this.delegate.deserialize(p, ctxt);
        }
        else {
            newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
        }
        if (newValue != oldValue && newValue != null) {
            return this.delegate.setAndReturn(instance, newValue);
        }
        return instance;
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        if (value != null) {
            this.delegate.set(instance, value);
        }
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        if (value != null) {
            return this.delegate.setAndReturn(instance, value);
        }
        return instance;
    }
}
