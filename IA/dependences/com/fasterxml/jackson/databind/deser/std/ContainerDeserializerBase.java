// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.lang.reflect.InvocationTargetException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public abstract class ContainerDeserializerBase<T> extends StdDeserializer<T> implements ValueInstantiator.Gettable
{
    protected final JavaType _containerType;
    protected final NullValueProvider _nullProvider;
    protected final Boolean _unwrapSingle;
    protected final boolean _skipNullValues;
    
    protected ContainerDeserializerBase(final JavaType selfType, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(selfType);
        this._containerType = selfType;
        this._unwrapSingle = unwrapSingle;
        this._nullProvider = nuller;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }
    
    protected ContainerDeserializerBase(final JavaType selfType) {
        this(selfType, null, null);
    }
    
    protected ContainerDeserializerBase(final ContainerDeserializerBase<?> base) {
        this(base, base._nullProvider, base._unwrapSingle);
    }
    
    protected ContainerDeserializerBase(final ContainerDeserializerBase<?> base, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(base._containerType);
        this._containerType = base._containerType;
        this._nullProvider = nuller;
        this._unwrapSingle = unwrapSingle;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }
    
    @Override
    public JavaType getValueType() {
        return this._containerType;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String refName) {
        final JsonDeserializer<Object> valueDeser = this.getContentDeserializer();
        if (valueDeser == null) {
            throw new IllegalArgumentException(String.format("Cannot handle managed/back reference '%s': type: container deserializer of type %s returned null for 'getContentDeserializer()'", refName, this.getClass().getName()));
        }
        return valueDeser.findBackReference(refName);
    }
    
    public JavaType getContentType() {
        if (this._containerType == null) {
            return TypeFactory.unknownType();
        }
        return this._containerType.getContentType();
    }
    
    public abstract JsonDeserializer<Object> getContentDeserializer();
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return null;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        final ValueInstantiator vi = this.getValueInstantiator();
        if (vi == null || !vi.canCreateUsingDefault()) {
            final JavaType type = this.getValueType();
            ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
        }
        try {
            return vi.createUsingDefault(ctxt);
        }
        catch (IOException e) {
            return ClassUtil.throwAsMappingException(ctxt, e);
        }
    }
    
    protected <BOGUS> BOGUS wrapAndThrow(Throwable t, final Object ref, final String key) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        if (t instanceof IOException && !(t instanceof JsonMappingException)) {
            throw (IOException)t;
        }
        throw JsonMappingException.wrapWithPath(t, ref, ClassUtil.nonNull(key, "N/A"));
    }
}
