// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.util.BitSet;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;

public class PropertyValueBuffer
{
    protected final JsonParser _parser;
    protected final DeserializationContext _context;
    protected final ObjectIdReader _objectIdReader;
    protected final Object[] _creatorParameters;
    protected int _paramsNeeded;
    protected int _paramsSeen;
    protected final BitSet _paramsSeenBig;
    protected PropertyValue _buffered;
    protected Object _idValue;
    
    public PropertyValueBuffer(final JsonParser p, final DeserializationContext ctxt, final int paramCount, final ObjectIdReader oir) {
        this._parser = p;
        this._context = ctxt;
        this._paramsNeeded = paramCount;
        this._objectIdReader = oir;
        this._creatorParameters = new Object[paramCount];
        if (paramCount < 32) {
            this._paramsSeenBig = null;
        }
        else {
            this._paramsSeenBig = new BitSet();
        }
    }
    
    public final boolean hasParameter(final SettableBeanProperty prop) {
        if (this._paramsSeenBig == null) {
            return (this._paramsSeen >> prop.getCreatorIndex() & 0x1) == 0x1;
        }
        return this._paramsSeenBig.get(prop.getCreatorIndex());
    }
    
    public Object getParameter(final SettableBeanProperty prop) throws JsonMappingException {
        Object value;
        if (this.hasParameter(prop)) {
            value = this._creatorParameters[prop.getCreatorIndex()];
        }
        else {
            final Object[] creatorParameters = this._creatorParameters;
            final int creatorIndex = prop.getCreatorIndex();
            final Object findMissing = this._findMissing(prop);
            creatorParameters[creatorIndex] = findMissing;
            value = findMissing;
        }
        if (value == null && this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            return this._context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS` enabled", prop.getName(), prop.getCreatorIndex());
        }
        return value;
    }
    
    public Object[] getParameters(final SettableBeanProperty[] props) throws JsonMappingException {
        if (this._paramsNeeded > 0) {
            if (this._paramsSeenBig == null) {
                for (int mask = this._paramsSeen, ix = 0, len = this._creatorParameters.length; ix < len; ++ix, mask >>= 1) {
                    if ((mask & 0x1) == 0x0) {
                        this._creatorParameters[ix] = this._findMissing(props[ix]);
                    }
                }
            }
            else {
                for (int len2 = this._creatorParameters.length, ix = 0; (ix = this._paramsSeenBig.nextClearBit(ix)) < len2; ++ix) {
                    this._creatorParameters[ix] = this._findMissing(props[ix]);
                }
            }
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            for (int ix2 = 0; ix2 < props.length; ++ix2) {
                if (this._creatorParameters[ix2] == null) {
                    final SettableBeanProperty prop = props[ix2];
                    this._context.reportInputMismatch(prop.getType(), "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS` enabled", prop.getName(), props[ix2].getCreatorIndex());
                }
            }
        }
        return this._creatorParameters;
    }
    
    protected Object _findMissing(final SettableBeanProperty prop) throws JsonMappingException {
        final Object injectableValueId = prop.getInjectableValueId();
        if (injectableValueId != null) {
            return this._context.findInjectableValue(prop.getInjectableValueId(), prop, null);
        }
        if (prop.isRequired()) {
            this._context.reportInputMismatch(prop, "Missing required creator property '%s' (index %d)", prop.getName(), prop.getCreatorIndex());
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
            this._context.reportInputMismatch(prop, "Missing creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES` enabled", prop.getName(), prop.getCreatorIndex());
        }
        final JsonDeserializer<Object> deser = prop.getValueDeserializer();
        return deser.getNullValue(this._context);
    }
    
    public boolean readIdProperty(final String propName) throws IOException {
        if (this._objectIdReader != null && propName.equals(this._objectIdReader.propertyName.getSimpleName())) {
            this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
            return true;
        }
        return false;
    }
    
    public Object handleIdValue(final DeserializationContext ctxt, final Object bean) throws IOException {
        if (this._objectIdReader != null) {
            if (this._idValue != null) {
                final ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
                roid.bindItem(bean);
                final SettableBeanProperty idProp = this._objectIdReader.idProperty;
                if (idProp != null) {
                    return idProp.setAndReturn(bean, this._idValue);
                }
            }
            else {
                ctxt.reportUnresolvedObjectId(this._objectIdReader, bean);
            }
        }
        return bean;
    }
    
    protected PropertyValue buffered() {
        return this._buffered;
    }
    
    public boolean isComplete() {
        return this._paramsNeeded <= 0;
    }
    
    public boolean assignParameter(final SettableBeanProperty prop, final Object value) {
        final int ix = prop.getCreatorIndex();
        this._creatorParameters[ix] = value;
        if (this._paramsSeenBig == null) {
            final int old = this._paramsSeen;
            final int newValue = old | 1 << ix;
            if (old != newValue) {
                this._paramsSeen = newValue;
                if (--this._paramsNeeded <= 0) {
                    return this._objectIdReader == null || this._idValue != null;
                }
            }
        }
        else if (!this._paramsSeenBig.get(ix)) {
            this._paramsSeenBig.set(ix);
            if (--this._paramsNeeded <= 0) {}
        }
        return false;
    }
    
    public void bufferProperty(final SettableBeanProperty prop, final Object value) {
        this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
    }
    
    public void bufferAnyProperty(final SettableAnyProperty prop, final String propName, final Object value) {
        this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
    }
    
    public void bufferMapProperty(final Object key, final Object value) {
        this._buffered = new PropertyValue.Map(this._buffered, value, key);
    }
}
