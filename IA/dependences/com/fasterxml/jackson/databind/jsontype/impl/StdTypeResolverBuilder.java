// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder>
{
    protected JsonTypeInfo.Id _idType;
    protected JsonTypeInfo.As _includeAs;
    protected String _typeProperty;
    protected boolean _typeIdVisible;
    protected Class<?> _defaultImpl;
    protected TypeIdResolver _customIdResolver;
    
    public StdTypeResolverBuilder() {
        this._typeIdVisible = false;
    }
    
    protected StdTypeResolverBuilder(final JsonTypeInfo.Id idType, final JsonTypeInfo.As idAs, final String propName) {
        this._typeIdVisible = false;
        this._idType = idType;
        this._includeAs = idAs;
        this._typeProperty = propName;
    }
    
    public static StdTypeResolverBuilder noTypeInfoBuilder() {
        return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, (TypeIdResolver)null);
    }
    
    @Override
    public StdTypeResolverBuilder init(final JsonTypeInfo.Id idType, final TypeIdResolver idRes) {
        if (idType == null) {
            throw new IllegalArgumentException("idType cannot be null");
        }
        this._idType = idType;
        this._customIdResolver = idRes;
        this._typeProperty = idType.getDefaultPropertyName();
        return this;
    }
    
    @Override
    public TypeSerializer buildTypeSerializer(final SerializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        if (baseType.isPrimitive()) {
            return null;
        }
        final TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, true, false);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeSerializer(idRes, null);
            }
            case PROPERTY: {
                return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeSerializer(idRes, null);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
            }
            case EXISTING_PROPERTY: {
                return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
            }
        }
    }
    
    @Override
    public TypeDeserializer buildTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        if (baseType.isPrimitive()) {
            return null;
        }
        final TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, false, true);
        final JavaType defaultImpl = this.defineDefaultImpl(config, baseType);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            }
            case PROPERTY:
            case EXISTING_PROPERTY: {
                return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
            }
        }
    }
    
    protected JavaType defineDefaultImpl(final DeserializationConfig config, final JavaType baseType) {
        JavaType defaultImpl;
        if (this._defaultImpl == null) {
            if (config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && !baseType.isAbstract()) {
                defaultImpl = baseType;
            }
            else {
                defaultImpl = null;
            }
        }
        else if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class) {
            defaultImpl = config.getTypeFactory().constructType(this._defaultImpl);
        }
        else if (baseType.hasRawClass(this._defaultImpl)) {
            defaultImpl = baseType;
        }
        else if (baseType.isTypeOrSuperTypeOf(this._defaultImpl)) {
            defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
        }
        else {
            defaultImpl = null;
        }
        return defaultImpl;
    }
    
    @Override
    public StdTypeResolverBuilder inclusion(final JsonTypeInfo.As includeAs) {
        if (includeAs == null) {
            throw new IllegalArgumentException("includeAs cannot be null");
        }
        this._includeAs = includeAs;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = typeIdPropName;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder defaultImpl(final Class<?> defaultImpl) {
        this._defaultImpl = defaultImpl;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder typeIdVisibility(final boolean isVisible) {
        this._typeIdVisible = isVisible;
        return this;
    }
    
    @Override
    public Class<?> getDefaultImpl() {
        return this._defaultImpl;
    }
    
    public String getTypeProperty() {
        return this._typeProperty;
    }
    
    public boolean isTypeIdVisible() {
        return this._typeIdVisible;
    }
    
    protected TypeIdResolver idResolver(final MapperConfig<?> config, final JavaType baseType, final Collection<NamedType> subtypes, final boolean forSer, final boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        if (this._idType == null) {
            throw new IllegalStateException("Cannot build, 'init()' not yet called");
        }
        switch (this._idType) {
            case CLASS: {
                return new ClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case MINIMAL_CLASS: {
                return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case NAME: {
                return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            }
            case NONE: {
                return null;
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
            }
        }
    }
}
