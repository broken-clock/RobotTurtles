// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.deser.impl.FailingDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;

public abstract class SettableBeanProperty extends ConcreteBeanPropertyBase implements Serializable
{
    protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER;
    protected final PropertyName _propName;
    protected final JavaType _type;
    protected final PropertyName _wrapperName;
    protected final transient Annotations _contextAnnotations;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final NullValueProvider _nullProvider;
    protected String _managedReferenceName;
    protected ObjectIdInfo _objectIdInfo;
    protected ViewMatcher _viewMatcher;
    protected int _propertyIndex;
    
    protected SettableBeanProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations) {
        this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef.getMetadata());
    }
    
    protected SettableBeanProperty(final PropertyName propName, final JavaType type, final PropertyName wrapper, TypeDeserializer typeDeser, final Annotations contextAnnotations, final PropertyMetadata metadata) {
        super(metadata);
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        }
        else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = wrapper;
        this._contextAnnotations = contextAnnotations;
        this._viewMatcher = null;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(this);
        }
        this._valueTypeDeserializer = typeDeser;
        this._valueDeserializer = SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
        this._nullProvider = SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
    }
    
    protected SettableBeanProperty(final PropertyName propName, final JavaType type, final PropertyMetadata metadata, final JsonDeserializer<Object> valueDeser) {
        super(metadata);
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        }
        else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = null;
        this._contextAnnotations = null;
        this._viewMatcher = null;
        this._valueTypeDeserializer = null;
        this._valueDeserializer = valueDeser;
        this._nullProvider = valueDeser;
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src) {
        super(src);
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
        this._nullProvider = src._nullProvider;
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src, final JsonDeserializer<?> deser, NullValueProvider nuller) {
        super(src);
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        if (deser == null) {
            this._valueDeserializer = SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
        }
        else {
            this._valueDeserializer = (JsonDeserializer<Object>)deser;
        }
        this._viewMatcher = src._viewMatcher;
        if (nuller == SettableBeanProperty.MISSING_VALUE_DESERIALIZER) {
            nuller = this._valueDeserializer;
        }
        this._nullProvider = nuller;
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src, final PropertyName newName) {
        super(src);
        this._propertyIndex = -1;
        this._propName = newName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
        this._nullProvider = src._nullProvider;
    }
    
    public abstract SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> p0);
    
    public abstract SettableBeanProperty withName(final PropertyName p0);
    
    public SettableBeanProperty withSimpleName(final String simpleName) {
        final PropertyName n = (this._propName == null) ? new PropertyName(simpleName) : this._propName.withSimpleName(simpleName);
        return (n == this._propName) ? this : this.withName(n);
    }
    
    public abstract SettableBeanProperty withNullProvider(final NullValueProvider p0);
    
    public void setManagedReferenceName(final String n) {
        this._managedReferenceName = n;
    }
    
    public void setObjectIdInfo(final ObjectIdInfo objectIdInfo) {
        this._objectIdInfo = objectIdInfo;
    }
    
    public void setViews(final Class<?>[] views) {
        if (views == null) {
            this._viewMatcher = null;
        }
        else {
            this._viewMatcher = ViewMatcher.construct(views);
        }
    }
    
    public void assignIndex(final int index) {
        if (this._propertyIndex != -1) {
            throw new IllegalStateException("Property '" + this.getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
        }
        this._propertyIndex = index;
    }
    
    public void fixAccess(final DeserializationConfig config) {
    }
    
    public void markAsIgnorable() {
    }
    
    public boolean isIgnorable() {
        return false;
    }
    
    @Override
    public final String getName() {
        return this._propName.getSimpleName();
    }
    
    @Override
    public PropertyName getFullName() {
        return this._propName;
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
    public abstract AnnotatedMember getMember();
    
    @Override
    public abstract <A extends Annotation> A getAnnotation(final Class<A> p0);
    
    @Override
    public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
        return this._contextAnnotations.get(acls);
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
        if (this.isRequired()) {
            objectVisitor.property(this);
        }
        else {
            objectVisitor.optionalProperty(this);
        }
    }
    
    protected Class<?> getDeclaringClass() {
        return this.getMember().getDeclaringClass();
    }
    
    public String getManagedReferenceName() {
        return this._managedReferenceName;
    }
    
    public ObjectIdInfo getObjectIdInfo() {
        return this._objectIdInfo;
    }
    
    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null && this._valueDeserializer != SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
    }
    
    public boolean hasValueTypeDeserializer() {
        return this._valueTypeDeserializer != null;
    }
    
    public JsonDeserializer<Object> getValueDeserializer() {
        final JsonDeserializer<Object> deser = this._valueDeserializer;
        if (deser == SettableBeanProperty.MISSING_VALUE_DESERIALIZER) {
            return null;
        }
        return deser;
    }
    
    public TypeDeserializer getValueTypeDeserializer() {
        return this._valueTypeDeserializer;
    }
    
    public NullValueProvider getNullValueProvider() {
        return this._nullProvider;
    }
    
    public boolean visibleInView(final Class<?> activeView) {
        return this._viewMatcher == null || this._viewMatcher.isVisibleForView(activeView);
    }
    
    public boolean hasViews() {
        return this._viewMatcher != null;
    }
    
    public int getPropertyIndex() {
        return this._propertyIndex;
    }
    
    public int getCreatorIndex() {
        throw new IllegalStateException(String.format("Internal error: no creator index for property '%s' (of type %s)", this.getName(), this.getClass().getName()));
    }
    
    public Object getInjectableValueId() {
        return null;
    }
    
    public abstract void deserializeAndSet(final JsonParser p0, final DeserializationContext p1, final Object p2) throws IOException;
    
    public abstract Object deserializeSetAndReturn(final JsonParser p0, final DeserializationContext p1, final Object p2) throws IOException;
    
    public abstract void set(final Object p0, final Object p1) throws IOException;
    
    public abstract Object setAndReturn(final Object p0, final Object p1) throws IOException;
    
    public final Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return this._nullProvider.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        Object value = this._valueDeserializer.deserialize(p, ctxt);
        if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
        }
        return value;
    }
    
    public final Object deserializeWith(final JsonParser p, final DeserializationContext ctxt, final Object toUpdate) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._valueTypeDeserializer != null) {
                ctxt.reportBadDefinition(this.getType(), String.format("Cannot merge polymorphic property '%s'", this.getName()));
            }
            Object value = this._valueDeserializer.deserialize(p, ctxt, toUpdate);
            if (value == null) {
                if (NullsConstantProvider.isSkipper(this._nullProvider)) {
                    return toUpdate;
                }
                value = this._nullProvider.getNullValue(ctxt);
            }
            return value;
        }
        if (NullsConstantProvider.isSkipper(this._nullProvider)) {
            return toUpdate;
        }
        return this._nullProvider.getNullValue(ctxt);
    }
    
    protected void _throwAsIOE(final JsonParser p, final Exception e, final Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            final String actType = ClassUtil.classNameOf(value);
            final StringBuilder msg = new StringBuilder("Problem deserializing property '").append(this.getName()).append("' (expected type: ").append(this.getType()).append("; actual type: ").append(actType).append(")");
            final String origMsg = ClassUtil.exceptionMessage(e);
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            }
            else {
                msg.append(" (no error message provided)");
            }
            throw JsonMappingException.from(p, msg.toString(), e);
        }
        this._throwAsIOE(p, e);
    }
    
    protected IOException _throwAsIOE(final JsonParser p, final Exception e) throws IOException {
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        final Throwable th = ClassUtil.getRootCause(e);
        throw JsonMappingException.from(p, ClassUtil.exceptionMessage(th), th);
    }
    
    @Deprecated
    protected IOException _throwAsIOE(final Exception e) throws IOException {
        return this._throwAsIOE(null, e);
    }
    
    protected void _throwAsIOE(final Exception e, final Object value) throws IOException {
        this._throwAsIOE(null, e, value);
    }
    
    @Override
    public String toString() {
        return "[property '" + this.getName() + "']";
    }
    
    static {
        MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
    }
    
    public abstract static class Delegating extends SettableBeanProperty
    {
        protected final SettableBeanProperty delegate;
        
        protected Delegating(final SettableBeanProperty d) {
            super(d);
            this.delegate = d;
        }
        
        protected abstract SettableBeanProperty withDelegate(final SettableBeanProperty p0);
        
        protected SettableBeanProperty _with(final SettableBeanProperty newDelegate) {
            if (newDelegate == this.delegate) {
                return this;
            }
            return this.withDelegate(newDelegate);
        }
        
        @Override
        public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
            return this._with(this.delegate.withValueDeserializer(deser));
        }
        
        @Override
        public SettableBeanProperty withName(final PropertyName newName) {
            return this._with(this.delegate.withName(newName));
        }
        
        @Override
        public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
            return this._with(this.delegate.withNullProvider(nva));
        }
        
        @Override
        public void assignIndex(final int index) {
            this.delegate.assignIndex(index);
        }
        
        @Override
        public void fixAccess(final DeserializationConfig config) {
            this.delegate.fixAccess(config);
        }
        
        @Override
        protected Class<?> getDeclaringClass() {
            return this.delegate.getDeclaringClass();
        }
        
        @Override
        public String getManagedReferenceName() {
            return this.delegate.getManagedReferenceName();
        }
        
        @Override
        public ObjectIdInfo getObjectIdInfo() {
            return this.delegate.getObjectIdInfo();
        }
        
        @Override
        public boolean hasValueDeserializer() {
            return this.delegate.hasValueDeserializer();
        }
        
        @Override
        public boolean hasValueTypeDeserializer() {
            return this.delegate.hasValueTypeDeserializer();
        }
        
        @Override
        public JsonDeserializer<Object> getValueDeserializer() {
            return this.delegate.getValueDeserializer();
        }
        
        @Override
        public TypeDeserializer getValueTypeDeserializer() {
            return this.delegate.getValueTypeDeserializer();
        }
        
        @Override
        public boolean visibleInView(final Class<?> activeView) {
            return this.delegate.visibleInView(activeView);
        }
        
        @Override
        public boolean hasViews() {
            return this.delegate.hasViews();
        }
        
        @Override
        public int getPropertyIndex() {
            return this.delegate.getPropertyIndex();
        }
        
        @Override
        public int getCreatorIndex() {
            return this.delegate.getCreatorIndex();
        }
        
        @Override
        public Object getInjectableValueId() {
            return this.delegate.getInjectableValueId();
        }
        
        @Override
        public AnnotatedMember getMember() {
            return this.delegate.getMember();
        }
        
        @Override
        public <A extends Annotation> A getAnnotation(final Class<A> acls) {
            return this.delegate.getAnnotation(acls);
        }
        
        public SettableBeanProperty getDelegate() {
            return this.delegate;
        }
        
        @Override
        public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
            this.delegate.deserializeAndSet(p, ctxt, instance);
        }
        
        @Override
        public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
            return this.delegate.deserializeSetAndReturn(p, ctxt, instance);
        }
        
        @Override
        public void set(final Object instance, final Object value) throws IOException {
            this.delegate.set(instance, value);
        }
        
        @Override
        public Object setAndReturn(final Object instance, final Object value) throws IOException {
            return this.delegate.setAndReturn(instance, value);
        }
    }
}
