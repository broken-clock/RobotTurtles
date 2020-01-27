// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class ReferenceTypeSerializer<T> extends StdSerializer<T> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;
    public static final Object MARKER_FOR_EMPTY;
    protected final JavaType _referredType;
    protected final BeanProperty _property;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final NameTransformer _unwrapper;
    protected transient PropertySerializerMap _dynamicSerializers;
    protected final Object _suppressableValue;
    protected final boolean _suppressNulls;
    
    public ReferenceTypeSerializer(final ReferenceType fullType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> ser) {
        super(fullType);
        this._referredType = fullType.getReferencedType();
        this._property = null;
        this._valueTypeSerializer = vts;
        this._valueSerializer = ser;
        this._unwrapper = null;
        this._suppressableValue = null;
        this._suppressNulls = false;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }
    
    protected ReferenceTypeSerializer(final ReferenceTypeSerializer<?> base, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSer, final NameTransformer unwrapper, final Object suppressableValue, final boolean suppressNulls) {
        super(base);
        this._referredType = base._referredType;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._property = property;
        this._valueTypeSerializer = vts;
        this._valueSerializer = (JsonSerializer<Object>)valueSer;
        this._unwrapper = unwrapper;
        this._suppressableValue = suppressableValue;
        this._suppressNulls = suppressNulls;
    }
    
    @Override
    public JsonSerializer<T> unwrappingSerializer(final NameTransformer transformer) {
        JsonSerializer<Object> valueSer = this._valueSerializer;
        if (valueSer != null) {
            valueSer = valueSer.unwrappingSerializer(transformer);
        }
        final NameTransformer unwrapper = (this._unwrapper == null) ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
        if (this._valueSerializer == valueSer && this._unwrapper == unwrapper) {
            return this;
        }
        return this.withResolved(this._property, this._valueTypeSerializer, valueSer, unwrapper);
    }
    
    protected abstract ReferenceTypeSerializer<T> withResolved(final BeanProperty p0, final TypeSerializer p1, final JsonSerializer<?> p2, final NameTransformer p3);
    
    public abstract ReferenceTypeSerializer<T> withContentInclusion(final Object p0, final boolean p1);
    
    protected abstract boolean _isValuePresent(final T p0);
    
    protected abstract Object _getReferenced(final T p0);
    
    protected abstract Object _getReferencedIfPresent(final T p0);
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = this.findAnnotatedContentSerializer(provider, property);
        if (ser == null) {
            ser = this._valueSerializer;
            if (ser == null) {
                if (this._useStatic(provider, property, this._referredType)) {
                    ser = this._findSerializer(provider, this._referredType, property);
                }
            }
            else {
                ser = provider.handlePrimaryContextualization(ser, property);
            }
        }
        ReferenceTypeSerializer<?> refSer;
        if (this._property == property && this._valueTypeSerializer == typeSer && this._valueSerializer == ser) {
            refSer = this;
        }
        else {
            refSer = this.withResolved(property, typeSer, ser, this._unwrapper);
        }
        if (property != null) {
            final JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), this.handledType());
            if (inclV != null) {
                final JsonInclude.Include incl = inclV.getContentInclusion();
                if (incl != JsonInclude.Include.USE_DEFAULTS) {
                    Object valueToSuppress = null;
                    boolean suppressNulls = false;
                    switch (incl) {
                        case NON_DEFAULT: {
                            valueToSuppress = BeanUtil.getDefaultValue(this._referredType);
                            suppressNulls = true;
                            if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                                valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                                break;
                            }
                            break;
                        }
                        case NON_ABSENT: {
                            suppressNulls = true;
                            valueToSuppress = (this._referredType.isReferenceType() ? ReferenceTypeSerializer.MARKER_FOR_EMPTY : null);
                            break;
                        }
                        case NON_EMPTY: {
                            suppressNulls = true;
                            valueToSuppress = ReferenceTypeSerializer.MARKER_FOR_EMPTY;
                            break;
                        }
                        case CUSTOM: {
                            valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
                            suppressNulls = (valueToSuppress == null || provider.includeFilterSuppressNulls(valueToSuppress));
                            break;
                        }
                        case NON_NULL: {
                            valueToSuppress = null;
                            suppressNulls = true;
                            break;
                        }
                        default: {
                            valueToSuppress = null;
                            suppressNulls = false;
                            break;
                        }
                    }
                    if (this._suppressableValue != valueToSuppress || this._suppressNulls != suppressNulls) {
                        refSer = refSer.withContentInclusion(valueToSuppress, suppressNulls);
                    }
                }
            }
        }
        return refSer;
    }
    
    protected boolean _useStatic(final SerializerProvider provider, final BeanProperty property, final JavaType referredType) {
        if (referredType.isJavaLangObject()) {
            return false;
        }
        if (referredType.isFinal()) {
            return true;
        }
        if (referredType.useStaticType()) {
            return true;
        }
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null && property != null) {
            final Annotated ann = property.getMember();
            if (ann != null) {
                final JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
                if (t == JsonSerialize.Typing.STATIC) {
                    return true;
                }
                if (t == JsonSerialize.Typing.DYNAMIC) {
                    return false;
                }
            }
        }
        return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider provider, final T value) {
        if (!this._isValuePresent(value)) {
            return true;
        }
        final Object contents = this._getReferenced(value);
        if (contents == null) {
            return this._suppressNulls;
        }
        if (this._suppressableValue == null) {
            return false;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            try {
                ser = this._findCachedSerializer(provider, contents.getClass());
            }
            catch (JsonMappingException e) {
                throw new RuntimeJsonMappingException(e);
            }
        }
        if (this._suppressableValue == ReferenceTypeSerializer.MARKER_FOR_EMPTY) {
            return ser.isEmpty(provider, contents);
        }
        return this._suppressableValue.equals(contents);
    }
    
    @Override
    public boolean isUnwrappingSerializer() {
        return this._unwrapper != null;
    }
    
    public JavaType getReferredType() {
        return this._referredType;
    }
    
    @Override
    public void serialize(final T ref, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final Object value = this._getReferencedIfPresent(ref);
        if (value == null) {
            if (this._unwrapper == null) {
                provider.defaultSerializeNull(g);
            }
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = this._findCachedSerializer(provider, value.getClass());
        }
        if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
        }
        else {
            ser.serialize(value, g, provider);
        }
    }
    
    @Override
    public void serializeWithType(final T ref, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final Object value = this._getReferencedIfPresent(ref);
        if (value == null) {
            if (this._unwrapper == null) {
                provider.defaultSerializeNull(g);
            }
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = this._findCachedSerializer(provider, value.getClass());
        }
        ser.serializeWithType(value, g, provider, typeSer);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser == null) {
            ser = this._findSerializer(visitor.getProvider(), this._referredType, this._property);
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
        }
        ser.acceptJsonFormatVisitor(visitor, this._referredType);
    }
    
    private final JsonSerializer<Object> _findCachedSerializer(final SerializerProvider provider, final Class<?> rawType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(rawType);
        if (ser == null) {
            if (this._referredType.hasGenericTypes()) {
                final JavaType fullType = provider.constructSpecializedType(this._referredType, rawType);
                ser = provider.findValueSerializer(fullType, this._property);
            }
            else {
                ser = provider.findValueSerializer(rawType, this._property);
            }
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
            this._dynamicSerializers = this._dynamicSerializers.newWith(rawType, ser);
        }
        return ser;
    }
    
    private final JsonSerializer<Object> _findSerializer(final SerializerProvider provider, final JavaType type, final BeanProperty prop) throws JsonMappingException {
        return provider.findValueSerializer(type, prop);
    }
    
    static {
        MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    }
}
