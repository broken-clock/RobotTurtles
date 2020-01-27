// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;

public class PropertyBuilder
{
    private static final Object NO_DEFAULT_MARKER;
    protected final SerializationConfig _config;
    protected final BeanDescription _beanDesc;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected Object _defaultBean;
    protected final JsonInclude.Value _defaultInclusion;
    protected final boolean _useRealPropertyDefaults;
    
    public PropertyBuilder(final SerializationConfig config, final BeanDescription beanDesc) {
        this._config = config;
        this._beanDesc = beanDesc;
        final JsonInclude.Value inclPerType = JsonInclude.Value.merge(beanDesc.findPropertyInclusion(JsonInclude.Value.empty()), config.getDefaultPropertyInclusion(beanDesc.getBeanClass(), JsonInclude.Value.empty()));
        this._defaultInclusion = JsonInclude.Value.merge(config.getDefaultPropertyInclusion(), inclPerType);
        this._useRealPropertyDefaults = (inclPerType.getValueInclusion() == JsonInclude.Include.NON_DEFAULT);
        this._annotationIntrospector = this._config.getAnnotationIntrospector();
    }
    
    public Annotations getClassAnnotations() {
        return this._beanDesc.getClassAnnotations();
    }
    
    protected BeanPropertyWriter buildWriter(final SerializerProvider prov, final BeanPropertyDefinition propDef, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final TypeSerializer contentTypeSer, final AnnotatedMember am, final boolean defaultUseStaticTyping) throws JsonMappingException {
        JavaType serializationType;
        try {
            serializationType = this.findSerializationType(am, defaultUseStaticTyping, declaredType);
        }
        catch (JsonMappingException e) {
            if (propDef == null) {
                return prov.reportBadDefinition(declaredType, ClassUtil.exceptionMessage(e));
            }
            return prov.reportBadPropertyDefinition(this._beanDesc, propDef, ClassUtil.exceptionMessage(e), new Object[0]);
        }
        if (contentTypeSer != null) {
            if (serializationType == null) {
                serializationType = declaredType;
            }
            JavaType ct = serializationType.getContentType();
            if (ct == null) {
                prov.reportBadPropertyDefinition(this._beanDesc, propDef, "serialization type " + serializationType + " has no content", new Object[0]);
            }
            serializationType = serializationType.withContentTypeHandler(contentTypeSer);
            ct = serializationType.getContentType();
        }
        Object valueToSuppress = null;
        boolean suppressNulls = false;
        final JavaType actualType = (serializationType == null) ? declaredType : serializationType;
        final AnnotatedMember accessor = propDef.getAccessor();
        if (accessor == null) {
            return prov.reportBadPropertyDefinition(this._beanDesc, propDef, "could not determine property type", new Object[0]);
        }
        final Class<?> rawPropertyType = accessor.getRawType();
        JsonInclude.Value inclV = this._config.getDefaultInclusion(actualType.getRawClass(), rawPropertyType, this._defaultInclusion);
        inclV = inclV.withOverrides(propDef.findInclusion());
        JsonInclude.Include inclusion = inclV.getValueInclusion();
        if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
            inclusion = JsonInclude.Include.ALWAYS;
        }
        Label_0499: {
            switch (inclusion) {
                case NON_DEFAULT: {
                    final Object defaultBean;
                    if (this._useRealPropertyDefaults && (defaultBean = this.getDefaultBean()) != null) {
                        if (prov.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                            am.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        try {
                            valueToSuppress = am.getValue(defaultBean);
                        }
                        catch (Exception e2) {
                            this._throwWrapped(e2, propDef.getName(), defaultBean);
                        }
                    }
                    else {
                        valueToSuppress = BeanUtil.getDefaultValue(actualType);
                        suppressNulls = true;
                    }
                    if (valueToSuppress == null) {
                        suppressNulls = true;
                        break Label_0499;
                    }
                    if (valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                    }
                    break Label_0499;
                }
                case NON_ABSENT: {
                    suppressNulls = true;
                    if (actualType.isReferenceType()) {
                        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                    }
                    break Label_0499;
                }
                case NON_EMPTY: {
                    suppressNulls = true;
                    valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                    break Label_0499;
                }
                case CUSTOM: {
                    valueToSuppress = prov.includeFilterInstance(propDef, inclV.getValueFilter());
                    suppressNulls = (valueToSuppress == null || prov.includeFilterSuppressNulls(valueToSuppress));
                    break Label_0499;
                }
                case NON_NULL: {
                    suppressNulls = true;
                    break;
                }
            }
            if (actualType.isContainerType() && !this._config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) {
                valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
            }
        }
        Class<?>[] views = propDef.findViews();
        if (views == null) {
            views = this._beanDesc.findDefaultViews();
        }
        BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress, views);
        final Object serDef = this._annotationIntrospector.findNullSerializer(am);
        if (serDef != null) {
            bpw.assignNullSerializer(prov.serializerInstance(am, serDef));
        }
        final NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
        if (unwrapper != null) {
            bpw = bpw.unwrappingWriter(unwrapper);
        }
        return bpw;
    }
    
    protected JavaType findSerializationType(final Annotated a, boolean useStaticTyping, JavaType declaredType) throws JsonMappingException {
        final JavaType secondary = this._annotationIntrospector.refineSerializationType(this._config, a, declaredType);
        if (secondary != declaredType) {
            final Class<?> serClass = secondary.getRawClass();
            final Class<?> rawDeclared = declaredType.getRawClass();
            if (!serClass.isAssignableFrom(rawDeclared)) {
                if (!rawDeclared.isAssignableFrom(serClass)) {
                    throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
                }
            }
            useStaticTyping = true;
            declaredType = secondary;
        }
        final JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
        if (typing != null && typing != JsonSerialize.Typing.DEFAULT_TYPING) {
            useStaticTyping = (typing == JsonSerialize.Typing.STATIC);
        }
        if (useStaticTyping) {
            return declaredType.withStaticTyping();
        }
        return null;
    }
    
    protected Object getDefaultBean() {
        Object def = this._defaultBean;
        if (def == null) {
            def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
            if (def == null) {
                def = PropertyBuilder.NO_DEFAULT_MARKER;
            }
            this._defaultBean = def;
        }
        return (def == PropertyBuilder.NO_DEFAULT_MARKER) ? null : this._defaultBean;
    }
    
    @Deprecated
    protected Object getPropertyDefaultValue(final String name, final AnnotatedMember member, final JavaType type) {
        final Object defaultBean = this.getDefaultBean();
        if (defaultBean == null) {
            return this.getDefaultValue(type);
        }
        try {
            return member.getValue(defaultBean);
        }
        catch (Exception e) {
            return this._throwWrapped(e, name, defaultBean);
        }
    }
    
    @Deprecated
    protected Object getDefaultValue(final JavaType type) {
        return BeanUtil.getDefaultValue(type);
    }
    
    protected Object _throwWrapped(final Exception e, final String propName, final Object defaultBean) {
        Throwable t;
        for (t = e; t.getCause() != null; t = t.getCause()) {}
        ClassUtil.throwIfError(t);
        ClassUtil.throwIfRTE(t);
        throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
    }
    
    static {
        NO_DEFAULT_MARKER = Boolean.FALSE;
    }
}
