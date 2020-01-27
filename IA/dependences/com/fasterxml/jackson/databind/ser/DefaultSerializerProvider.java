// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.Closeable;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.BeanProperty;
import java.util.concurrent.atomic.AtomicReference;
import java.util.IdentityHashMap;
import java.util.HashMap;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import java.util.Map;
import java.io.Serializable;
import com.fasterxml.jackson.databind.SerializerProvider;

public abstract class DefaultSerializerProvider extends SerializerProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient Map<Object, WritableObjectId> _seenObjectIds;
    protected transient ArrayList<ObjectIdGenerator<?>> _objectIdGenerators;
    protected transient JsonGenerator _generator;
    
    protected DefaultSerializerProvider() {
    }
    
    protected DefaultSerializerProvider(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        super(src, config, f);
    }
    
    protected DefaultSerializerProvider(final DefaultSerializerProvider src) {
        super(src);
    }
    
    public abstract DefaultSerializerProvider createInstance(final SerializationConfig p0, final SerializerFactory p1);
    
    public DefaultSerializerProvider copy() {
        throw new IllegalStateException("DefaultSerializerProvider sub-class not overriding copy()");
    }
    
    @Override
    public JsonSerializer<Object> serializerInstance(final Annotated annotated, final Object serDef) throws JsonMappingException {
        if (serDef == null) {
            return null;
        }
        JsonSerializer<?> ser;
        if (serDef instanceof JsonSerializer) {
            ser = (JsonSerializer<?>)serDef;
        }
        else {
            if (!(serDef instanceof Class)) {
                this.reportBadDefinition(annotated.getType(), "AnnotationIntrospector returned serializer definition of type " + serDef.getClass().getName() + "; expected type JsonSerializer or Class<JsonSerializer> instead");
            }
            final Class<?> serClass = (Class<?>)serDef;
            if (serClass == JsonSerializer.None.class || ClassUtil.isBogusClass(serClass)) {
                return null;
            }
            if (!JsonSerializer.class.isAssignableFrom(serClass)) {
                this.reportBadDefinition(annotated.getType(), "AnnotationIntrospector returned Class " + serClass.getName() + "; expected Class<JsonSerializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            ser = ((hi == null) ? null : hi.serializerInstance(this._config, annotated, serClass));
            if (ser == null) {
                ser = ClassUtil.createInstance(serClass, this._config.canOverrideAccessModifiers());
            }
        }
        return this._handleResolvable(ser);
    }
    
    @Override
    public Object includeFilterInstance(final BeanPropertyDefinition forProperty, final Class<?> filterClass) {
        if (filterClass == null) {
            return null;
        }
        final HandlerInstantiator hi = this._config.getHandlerInstantiator();
        Object filter = (hi == null) ? null : hi.includeFilterInstance(this._config, forProperty, filterClass);
        if (filter == null) {
            filter = ClassUtil.createInstance(filterClass, this._config.canOverrideAccessModifiers());
        }
        return filter;
    }
    
    @Override
    public boolean includeFilterSuppressNulls(final Object filter) throws JsonMappingException {
        if (filter == null) {
            return true;
        }
        try {
            return filter.equals(null);
        }
        catch (Throwable t) {
            final String msg = String.format("Problem determining whether filter of type '%s' should filter out `null` values: (%s) %s", filter.getClass().getName(), t.getClass().getName(), ClassUtil.exceptionMessage(t));
            this.reportBadDefinition(filter.getClass(), msg, t);
            return false;
        }
    }
    
    @Override
    public WritableObjectId findObjectId(final Object forPojo, final ObjectIdGenerator<?> generatorType) {
        if (this._seenObjectIds == null) {
            this._seenObjectIds = this._createObjectIdMap();
        }
        else {
            final WritableObjectId oid = this._seenObjectIds.get(forPojo);
            if (oid != null) {
                return oid;
            }
        }
        ObjectIdGenerator<?> generator = null;
        if (this._objectIdGenerators == null) {
            this._objectIdGenerators = new ArrayList<ObjectIdGenerator<?>>(8);
        }
        else {
            for (int i = 0, len = this._objectIdGenerators.size(); i < len; ++i) {
                final ObjectIdGenerator<?> gen = this._objectIdGenerators.get(i);
                if (gen.canUseFor(generatorType)) {
                    generator = gen;
                    break;
                }
            }
        }
        if (generator == null) {
            generator = generatorType.newForSerialization(this);
            this._objectIdGenerators.add(generator);
        }
        final WritableObjectId oid2 = new WritableObjectId(generator);
        this._seenObjectIds.put(forPojo, oid2);
        return oid2;
    }
    
    protected Map<Object, WritableObjectId> _createObjectIdMap() {
        if (this.isEnabled(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID)) {
            return new HashMap<Object, WritableObjectId>();
        }
        return new IdentityHashMap<Object, WritableObjectId>();
    }
    
    public boolean hasSerializerFor(final Class<?> cls, final AtomicReference<Throwable> cause) {
        if (cls == Object.class && !this._config.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
            return true;
        }
        try {
            final JsonSerializer<?> ser = this._findExplicitUntypedSerializer(cls);
            return ser != null;
        }
        catch (JsonMappingException e) {
            if (cause != null) {
                cause.set(e);
            }
        }
        catch (RuntimeException e2) {
            if (cause == null) {
                throw e2;
            }
            cause.set(e2);
        }
        return false;
    }
    
    @Override
    public JsonGenerator getGenerator() {
        return this._generator;
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeNull(gen);
            return;
        }
        final Class<?> cls = value.getClass();
        final JsonSerializer<Object> ser = this.findTypedValueSerializer(cls, true, null);
        final PropertyName rootName = this._config.getFullRootName();
        if (rootName == null) {
            if (this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE)) {
                this._serialize(gen, value, ser, this._config.findRootName(cls));
                return;
            }
        }
        else if (!rootName.isEmpty()) {
            this._serialize(gen, value, ser, rootName);
            return;
        }
        this._serialize(gen, value, ser);
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value, final JavaType rootType) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeNull(gen);
            return;
        }
        if (!rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        final JsonSerializer<Object> ser = this.findTypedValueSerializer(rootType, true, null);
        final PropertyName rootName = this._config.getFullRootName();
        if (rootName == null) {
            if (this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE)) {
                this._serialize(gen, value, ser, this._config.findRootName(rootType));
                return;
            }
        }
        else if (!rootName.isEmpty()) {
            this._serialize(gen, value, ser, rootName);
            return;
        }
        this._serialize(gen, value, ser);
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value, final JavaType rootType, JsonSerializer<Object> ser) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeNull(gen);
            return;
        }
        if (rootType != null && !rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        if (ser == null) {
            ser = this.findTypedValueSerializer(rootType, true, null);
        }
        PropertyName rootName = this._config.getFullRootName();
        if (rootName == null) {
            if (this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE)) {
                rootName = ((rootType == null) ? this._config.findRootName(value.getClass()) : this._config.findRootName(rootType));
                this._serialize(gen, value, ser, rootName);
                return;
            }
        }
        else if (!rootName.isEmpty()) {
            this._serialize(gen, value, ser, rootName);
            return;
        }
        this._serialize(gen, value, ser);
    }
    
    public void serializePolymorphic(final JsonGenerator gen, final Object value, final JavaType rootType, JsonSerializer<Object> valueSer, final TypeSerializer typeSer) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeNull(gen);
            return;
        }
        if (rootType != null && !rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        if (valueSer == null) {
            if (rootType != null && rootType.isContainerType()) {
                valueSer = this.findValueSerializer(rootType, null);
            }
            else {
                valueSer = this.findValueSerializer(value.getClass(), null);
            }
        }
        final PropertyName rootName = this._config.getFullRootName();
        boolean wrap;
        if (rootName == null) {
            wrap = this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
            if (wrap) {
                gen.writeStartObject();
                final PropertyName pname = this._config.findRootName(value.getClass());
                gen.writeFieldName(pname.simpleAsEncoded(this._config));
            }
        }
        else if (rootName.isEmpty()) {
            wrap = false;
        }
        else {
            wrap = true;
            gen.writeStartObject();
            gen.writeFieldName(rootName.getSimpleName());
        }
        try {
            valueSer.serializeWithType(value, gen, this, typeSer);
            if (wrap) {
                gen.writeEndObject();
            }
        }
        catch (Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
    }
    
    private final void _serialize(final JsonGenerator gen, final Object value, final JsonSerializer<Object> ser, final PropertyName rootName) throws IOException {
        try {
            gen.writeStartObject();
            gen.writeFieldName(rootName.simpleAsEncoded(this._config));
            ser.serialize(value, gen, this);
            gen.writeEndObject();
        }
        catch (Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
    }
    
    private final void _serialize(final JsonGenerator gen, final Object value, final JsonSerializer<Object> ser) throws IOException {
        try {
            ser.serialize(value, gen, this);
        }
        catch (Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
    }
    
    protected void _serializeNull(final JsonGenerator gen) throws IOException {
        final JsonSerializer<Object> ser = this.getDefaultNullValueSerializer();
        try {
            ser.serialize(null, gen, this);
        }
        catch (Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
    }
    
    private IOException _wrapAsIOE(final JsonGenerator g, final Exception e) {
        if (e instanceof IOException) {
            return (IOException)e;
        }
        String msg = ClassUtil.exceptionMessage(e);
        if (msg == null) {
            msg = "[no message for " + e.getClass().getName() + "]";
        }
        return new JsonMappingException(g, msg, e);
    }
    
    public int cachedSerializersCount() {
        return this._serializerCache.size();
    }
    
    public void flushCachedSerializers() {
        this._serializerCache.flush();
    }
    
    public void acceptJsonFormatVisitor(final JavaType javaType, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (javaType == null) {
            throw new IllegalArgumentException("A class must be provided");
        }
        visitor.setProvider(this);
        this.findValueSerializer(javaType, null).acceptJsonFormatVisitor(visitor, javaType);
    }
    
    @Deprecated
    public JsonSchema generateJsonSchema(final Class<?> type) throws JsonMappingException {
        final JsonSerializer<Object> ser = this.findValueSerializer(type, null);
        final JsonNode schemaNode = (ser instanceof SchemaAware) ? ((SchemaAware)ser).getSchema(this, null) : JsonSchema.getDefaultSchemaNode();
        if (!(schemaNode instanceof ObjectNode)) {
            throw new IllegalArgumentException("Class " + type.getName() + " would not be serialized as a JSON object and therefore has no schema");
        }
        return new JsonSchema((ObjectNode)schemaNode);
    }
    
    public static final class Impl extends DefaultSerializerProvider
    {
        private static final long serialVersionUID = 1L;
        
        public Impl() {
        }
        
        public Impl(final Impl src) {
            super(src);
        }
        
        protected Impl(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
            super(src, config, f);
        }
        
        @Override
        public DefaultSerializerProvider copy() {
            if (this.getClass() != Impl.class) {
                return super.copy();
            }
            return new Impl(this);
        }
        
        @Override
        public Impl createInstance(final SerializationConfig config, final SerializerFactory jsf) {
            return new Impl(this, config, jsf);
        }
    }
}
