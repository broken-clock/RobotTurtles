// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;

public final class DeserializerCache implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers;
    protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers;
    
    public DeserializerCache() {
        this._cachedDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.75f, 4);
        this._incompleteDeserializers = new HashMap<JavaType, JsonDeserializer<Object>>(8);
    }
    
    Object writeReplace() {
        this._incompleteDeserializers.clear();
        return this;
    }
    
    public int cachedDeserializersCount() {
        return this._cachedDeserializers.size();
    }
    
    public void flushCachedDeserializers() {
        this._cachedDeserializers.clear();
    }
    
    public JsonDeserializer<Object> findValueDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType propertyType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(propertyType);
        if (deser == null) {
            deser = this._createAndCacheValueDeserializer(ctxt, factory, propertyType);
            if (deser == null) {
                deser = this._handleUnknownValueDeserializer(ctxt, propertyType);
            }
        }
        return deser;
    }
    
    public KeyDeserializer findKeyDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        final KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
        if (kd == null) {
            return this._handleUnknownKeyDeserializer(ctxt, type);
        }
        if (kd instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)kd).resolve(ctxt);
        }
        return kd;
    }
    
    public boolean hasValueDeserializerFor(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
        if (deser == null) {
            deser = this._createAndCacheValueDeserializer(ctxt, factory, type);
        }
        return deser != null;
    }
    
    protected JsonDeserializer<Object> _findCachedDeserializer(final JavaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null JavaType passed");
        }
        if (this._hasCustomHandlers(type)) {
            return null;
        }
        return this._cachedDeserializers.get(type);
    }
    
    protected JsonDeserializer<Object> _createAndCacheValueDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        synchronized (this._incompleteDeserializers) {
            JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
            if (deser != null) {
                return deser;
            }
            final int count = this._incompleteDeserializers.size();
            if (count > 0) {
                deser = this._incompleteDeserializers.get(type);
                if (deser != null) {
                    return deser;
                }
            }
            try {
                return this._createAndCache2(ctxt, factory, type);
            }
            finally {
                if (count == 0 && this._incompleteDeserializers.size() > 0) {
                    this._incompleteDeserializers.clear();
                }
            }
        }
    }
    
    protected JsonDeserializer<Object> _createAndCache2(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        try {
            deser = this._createDeserializer(ctxt, factory, type);
        }
        catch (IllegalArgumentException iae) {
            throw JsonMappingException.from(ctxt, ClassUtil.exceptionMessage(iae), iae);
        }
        if (deser == null) {
            return null;
        }
        final boolean addToCache = !this._hasCustomHandlers(type) && deser.isCachable();
        if (deser instanceof ResolvableDeserializer) {
            this._incompleteDeserializers.put(type, deser);
            ((ResolvableDeserializer)deser).resolve(ctxt);
            this._incompleteDeserializers.remove(type);
        }
        if (addToCache) {
            this._cachedDeserializers.put(type, deser);
        }
        return deser;
    }
    
    protected JsonDeserializer<Object> _createDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
            type = factory.mapAbstractType(config, type);
        }
        BeanDescription beanDesc = config.introspect(type);
        final JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (deser != null) {
            return deser;
        }
        final JavaType newType = this.modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
        if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
        }
        final Class<?> builder = beanDesc.findPOJOBuilder();
        if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
        }
        final Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
        if (conv == null) {
            return (JsonDeserializer<Object>)this._createDeserializer2(ctxt, factory, type, beanDesc);
        }
        final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
        }
        return new StdDelegatingDeserializer<Object>(conv, delegateType, this._createDeserializer2(ctxt, factory, delegateType, beanDesc));
    }
    
    protected JsonDeserializer<?> _createDeserializer2(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        if (type.isEnumType()) {
            return factory.createEnumDeserializer(ctxt, type, beanDesc);
        }
        if (type.isContainerType()) {
            if (type.isArrayType()) {
                return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
            }
            if (type.isMapLikeType()) {
                final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
                if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
                    final MapLikeType mlt = (MapLikeType)type;
                    if (mlt.isTrueMapType()) {
                        return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
                    }
                    return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
                }
            }
            if (type.isCollectionLikeType()) {
                final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
                if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
                    final CollectionLikeType clt = (CollectionLikeType)type;
                    if (clt.isTrueCollectionType()) {
                        return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
                    }
                    return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
                }
            }
        }
        if (type.isReferenceType()) {
            return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc);
        }
        if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
            return factory.createTreeDeserializer(config, type, beanDesc);
        }
        return factory.createBeanDeserializer(ctxt, type, beanDesc);
    }
    
    protected JsonDeserializer<Object> findDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef == null) {
            return null;
        }
        final JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
        return this.findConvertingDeserializer(ctxt, ann, deser);
    }
    
    protected JsonDeserializer<Object> findConvertingDeserializer(final DeserializationContext ctxt, final Annotated a, final JsonDeserializer<Object> deser) throws JsonMappingException {
        final Converter<Object, Object> conv = this.findConverter(ctxt, a);
        if (conv == null) {
            return deser;
        }
        final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        return new StdDelegatingDeserializer<Object>(conv, delegateType, deser);
    }
    
    protected Converter<Object, Object> findConverter(final DeserializationContext ctxt, final Annotated a) throws JsonMappingException {
        final Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return ctxt.converterInstance(a, convDef);
    }
    
    private JavaType modifyTypeByAnnotation(final DeserializationContext ctxt, final Annotated a, JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null && keyType.getValueHandler() == null) {
                final Object kdDef = intr.findKeyDeserializer(a);
                if (kdDef != null) {
                    final KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                    if (kd != null) {
                        type = ((MapLikeType)type).withKeyValueHandler(kd);
                        keyType = type.getKeyType();
                    }
                }
            }
        }
        final JavaType contentType = type.getContentType();
        if (contentType != null && contentType.getValueHandler() == null) {
            Object cdDef = intr.findContentDeserializer(a);
            if (cdDef != null) {
                JsonDeserializer<?> cd = null;
                if (cdDef instanceof JsonDeserializer) {
                    cdDef = cdDef;
                }
                else {
                    final Class<?> cdClass = this._verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                    if (cdClass != null) {
                        cd = ctxt.deserializerInstance(a, cdClass);
                    }
                }
                if (cd != null) {
                    type = type.withContentValueHandler(cd);
                }
            }
        }
        type = intr.refineDeserializationType(ctxt.getConfig(), a, type);
        return type;
    }
    
    private boolean _hasCustomHandlers(final JavaType t) {
        if (t.isContainerType()) {
            final JavaType ct = t.getContentType();
            if (ct != null && (ct.getValueHandler() != null || ct.getTypeHandler() != null)) {
                return true;
            }
            if (t.isMapLikeType()) {
                final JavaType kt = t.getKeyType();
                if (kt.getValueHandler() != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Class<?> _verifyAsClass(final Object src, final String methodName, final Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (!(src instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        final Class<?> cls = (Class<?>)src;
        if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }
    
    protected JsonDeserializer<Object> _handleUnknownValueDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final Class<?> rawClass = type.getRawClass();
        if (!ClassUtil.isConcrete(rawClass)) {
            return ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for abstract type " + type);
        }
        return ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for type " + type);
    }
    
    protected KeyDeserializer _handleUnknownKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        return ctxt.reportBadDefinition(type, "Cannot find a (Map) Key deserializer for type " + type);
    }
}
