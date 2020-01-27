// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collections;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.util.Annotations;
import java.io.Serializable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import java.util.List;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.util.Named;

public interface BeanProperty extends Named
{
    public static final JsonFormat.Value EMPTY_FORMAT = new JsonFormat.Value();
    public static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();
    
    String getName();
    
    PropertyName getFullName();
    
    JavaType getType();
    
    PropertyName getWrapperName();
    
    PropertyMetadata getMetadata();
    
    boolean isRequired();
    
    boolean isVirtual();
    
     <A extends Annotation> A getAnnotation(final Class<A> p0);
    
     <A extends Annotation> A getContextAnnotation(final Class<A> p0);
    
    AnnotatedMember getMember();
    
    @Deprecated
    JsonFormat.Value findFormatOverrides(final AnnotationIntrospector p0);
    
    JsonFormat.Value findPropertyFormat(final MapperConfig<?> p0, final Class<?> p1);
    
    JsonInclude.Value findPropertyInclusion(final MapperConfig<?> p0, final Class<?> p1);
    
    List<PropertyName> findAliases(final MapperConfig<?> p0);
    
    void depositSchemaProperty(final JsonObjectFormatVisitor p0, final SerializerProvider p1) throws JsonMappingException;
    
    public static class Std implements BeanProperty, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final PropertyName _name;
        protected final JavaType _type;
        protected final PropertyName _wrapperName;
        protected final PropertyMetadata _metadata;
        protected final AnnotatedMember _member;
        
        public Std(final PropertyName name, final JavaType type, final PropertyName wrapperName, final AnnotatedMember member, final PropertyMetadata metadata) {
            this._name = name;
            this._type = type;
            this._wrapperName = wrapperName;
            this._metadata = metadata;
            this._member = member;
        }
        
        @Deprecated
        public Std(final PropertyName name, final JavaType type, final PropertyName wrapperName, final Annotations contextAnnotations, final AnnotatedMember member, final PropertyMetadata metadata) {
            this(name, type, wrapperName, member, metadata);
        }
        
        public Std(final Std base, final JavaType newType) {
            this(base._name, newType, base._wrapperName, base._member, base._metadata);
        }
        
        public Std withType(final JavaType type) {
            return new Std(this, type);
        }
        
        @Override
        public <A extends Annotation> A getAnnotation(final Class<A> acls) {
            return (A)((this._member == null) ? null : this._member.getAnnotation(acls));
        }
        
        @Override
        public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
            return null;
        }
        
        @Deprecated
        @Override
        public JsonFormat.Value findFormatOverrides(final AnnotationIntrospector intr) {
            if (this._member != null && intr != null) {
                final JsonFormat.Value v = intr.findFormat(this._member);
                if (v != null) {
                    return v;
                }
            }
            return Std.EMPTY_FORMAT;
        }
        
        @Override
        public JsonFormat.Value findPropertyFormat(final MapperConfig<?> config, final Class<?> baseType) {
            final JsonFormat.Value v0 = config.getDefaultPropertyFormat(baseType);
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr == null || this._member == null) {
                return v0;
            }
            final JsonFormat.Value v2 = intr.findFormat(this._member);
            if (v2 == null) {
                return v0;
            }
            return v0.withOverrides(v2);
        }
        
        @Override
        public JsonInclude.Value findPropertyInclusion(final MapperConfig<?> config, final Class<?> baseType) {
            final JsonInclude.Value v0 = config.getDefaultInclusion(baseType, this._type.getRawClass());
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr == null || this._member == null) {
                return v0;
            }
            final JsonInclude.Value v2 = intr.findPropertyInclusion(this._member);
            if (v2 == null) {
                return v0;
            }
            return v0.withOverrides(v2);
        }
        
        @Override
        public List<PropertyName> findAliases(final MapperConfig<?> config) {
            return Collections.emptyList();
        }
        
        @Override
        public String getName() {
            return this._name.getSimpleName();
        }
        
        @Override
        public PropertyName getFullName() {
            return this._name;
        }
        
        @Override
        public JavaType getType() {
            return this._type;
        }
        
        @Override
        public PropertyName getWrapperName() {
            return this._wrapperName;
        }
        
        @Override
        public boolean isRequired() {
            return this._metadata.isRequired();
        }
        
        @Override
        public PropertyMetadata getMetadata() {
            return this._metadata;
        }
        
        @Override
        public AnnotatedMember getMember() {
            return this._member;
        }
        
        @Override
        public boolean isVirtual() {
            return false;
        }
        
        @Override
        public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) {
            throw new UnsupportedOperationException("Instances of " + this.getClass().getName() + " should not get visited");
        }
    }
    
    public static class Bogus implements BeanProperty
    {
        @Override
        public String getName() {
            return "";
        }
        
        @Override
        public PropertyName getFullName() {
            return PropertyName.NO_NAME;
        }
        
        @Override
        public JavaType getType() {
            return TypeFactory.unknownType();
        }
        
        @Override
        public PropertyName getWrapperName() {
            return null;
        }
        
        @Override
        public PropertyMetadata getMetadata() {
            return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        
        @Override
        public boolean isRequired() {
            return false;
        }
        
        @Override
        public boolean isVirtual() {
            return false;
        }
        
        @Override
        public <A extends Annotation> A getAnnotation(final Class<A> acls) {
            return null;
        }
        
        @Override
        public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
            return null;
        }
        
        @Override
        public AnnotatedMember getMember() {
            return null;
        }
        
        @Deprecated
        @Override
        public JsonFormat.Value findFormatOverrides(final AnnotationIntrospector intr) {
            return JsonFormat.Value.empty();
        }
        
        @Override
        public JsonFormat.Value findPropertyFormat(final MapperConfig<?> config, final Class<?> baseType) {
            return JsonFormat.Value.empty();
        }
        
        @Override
        public JsonInclude.Value findPropertyInclusion(final MapperConfig<?> config, final Class<?> baseType) {
            return null;
        }
        
        @Override
        public List<PropertyName> findAliases(final MapperConfig<?> config) {
            return Collections.emptyList();
        }
        
        @Override
        public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
        }
    }
}
