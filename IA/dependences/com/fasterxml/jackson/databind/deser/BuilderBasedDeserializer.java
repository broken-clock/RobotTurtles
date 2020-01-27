// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Set;
import java.util.Map;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class BuilderBasedDeserializer extends BeanDeserializerBase
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedMethod _buildMethod;
    protected final JavaType _targetType;
    
    public BuilderBasedDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final JavaType targetType, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final Set<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
        this._targetType = targetType;
        this._buildMethod = builder.getBuildMethod();
        if (this._objectIdReader != null) {
            throw new IllegalArgumentException("Cannot use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")");
        }
    }
    
    @Deprecated
    public BuilderBasedDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final Set<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        this(builder, beanDesc, beanDesc.getType(), properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }
    
    protected BuilderBasedDeserializer(final BuilderBasedDeserializer src) {
        this(src, src._ignoreAllUnknown);
    }
    
    protected BuilderBasedDeserializer(final BuilderBasedDeserializer src, final boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }
    
    protected BuilderBasedDeserializer(final BuilderBasedDeserializer src, final NameTransformer unwrapper) {
        super(src, unwrapper);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }
    
    public BuilderBasedDeserializer(final BuilderBasedDeserializer src, final ObjectIdReader oir) {
        super(src, oir);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }
    
    public BuilderBasedDeserializer(final BuilderBasedDeserializer src, final Set<String> ignorableProps) {
        super(src, ignorableProps);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }
    
    public BuilderBasedDeserializer(final BuilderBasedDeserializer src, final BeanPropertyMap props) {
        super(src, props);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        return new BuilderBasedDeserializer(this, unwrapper);
    }
    
    @Override
    public BeanDeserializerBase withObjectIdReader(final ObjectIdReader oir) {
        return new BuilderBasedDeserializer(this, oir);
    }
    
    @Override
    public BeanDeserializerBase withIgnorableProperties(final Set<String> ignorableProps) {
        return new BuilderBasedDeserializer(this, ignorableProps);
    }
    
    @Override
    public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
        return new BuilderBasedDeserializer(this, props);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        final SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
        return new BeanAsArrayBuilderDeserializer(this, this._targetType, props, this._buildMethod);
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    protected Object finishBuild(final DeserializationContext ctxt, final Object builder) throws IOException {
        if (null == this._buildMethod) {
            return builder;
        }
        try {
            return this._buildMethod.getMember().invoke(builder, (Object[])null);
        }
        catch (Exception e) {
            return this.wrapInstantiationProblem(e, ctxt);
        }
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.isExpectedStartObjectToken()) {
            final JsonToken t = p.nextToken();
            if (this._vanillaProcessing) {
                return this.finishBuild(ctxt, this.vanillaDeserialize(p, ctxt, t));
            }
            final Object builder = this.deserializeFromObject(p, ctxt);
            return this.finishBuild(ctxt, builder);
        }
        else {
            switch (p.getCurrentTokenId()) {
                case 6: {
                    return this.finishBuild(ctxt, this.deserializeFromString(p, ctxt));
                }
                case 7: {
                    return this.finishBuild(ctxt, this.deserializeFromNumber(p, ctxt));
                }
                case 8: {
                    return this.finishBuild(ctxt, this.deserializeFromDouble(p, ctxt));
                }
                case 12: {
                    return p.getEmbeddedObject();
                }
                case 9:
                case 10: {
                    return this.finishBuild(ctxt, this.deserializeFromBoolean(p, ctxt));
                }
                case 3: {
                    return this.finishBuild(ctxt, this.deserializeFromArray(p, ctxt));
                }
                case 2:
                case 5: {
                    return this.finishBuild(ctxt, this.deserializeFromObject(p, ctxt));
                }
                default: {
                    return ctxt.handleUnexpectedToken(this.handledType(), p);
                }
            }
        }
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object value) throws IOException {
        final JavaType valueType = this._targetType;
        final Class<?> builderRawType = this.handledType();
        final Class<?> instRawType = value.getClass();
        if (builderRawType.isAssignableFrom(instRawType)) {
            return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing Builder (%s) instance not supported", valueType, builderRawType.getName()));
        }
        return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing instance (of %s) not supported", valueType, instRawType.getName()));
    }
    
    private final Object vanillaDeserialize(final JsonParser p, final DeserializationContext ctxt, final JsonToken t) throws IOException {
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, propName, ctxt);
                }
            }
            else {
                this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
            p.nextToken();
        }
        return bean;
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!this._nonStandardCreation) {
            Object bean = this._valueInstantiator.createUsingDefault(ctxt);
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            if (this._needViewProcesing) {
                final Class<?> view = ctxt.getActiveView();
                if (view != null) {
                    return this.deserializeWithView(p, ctxt, bean, view);
                }
            }
            while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                final String propName = p.getCurrentName();
                p.nextToken();
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
                else {
                    this.handleUnknownVanilla(p, ctxt, bean, propName);
                }
                p.nextToken();
            }
            return bean;
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(p, ctxt);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(p, ctxt);
        }
        return this.deserializeFromObjectUsingNonDefault(p, ctxt);
    }
    
    @Override
    protected Object _deserializeUsingPropertyBased(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        TokenBuffer unknown = null;
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (activeView != null && !creatorProp.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                    p.nextToken();
                    Object builder;
                    try {
                        builder = creator.build(ctxt, buffer);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                        continue;
                    }
                    if (builder.getClass() != this._beanType.getRawClass()) {
                        return this.handlePolymorphic(p, ctxt, builder, unknown);
                    }
                    if (unknown != null) {
                        builder = this.handleUnknownProperties(ctxt, builder, unknown);
                    }
                    return this._deserialize(p, ctxt, builder);
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
                }
                else if (this._anySetter != null) {
                    buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                }
                else {
                    if (unknown == null) {
                        unknown = new TokenBuffer(p, ctxt);
                    }
                    unknown.writeFieldName(propName);
                    unknown.copyCurrentStructure(p);
                }
            }
        }
        Object builder2;
        try {
            builder2 = creator.build(ctxt, buffer);
        }
        catch (Exception e2) {
            builder2 = this.wrapInstantiationProblem(e2, ctxt);
        }
        if (unknown == null) {
            return builder2;
        }
        if (builder2.getClass() != this._beanType.getRawClass()) {
            return this.handlePolymorphic(null, ctxt, builder2, unknown);
        }
        return this.handleUnknownProperties(ctxt, builder2, unknown);
    }
    
    protected final Object _deserialize(final JsonParser p, final DeserializationContext ctxt, Object builder) throws IOException {
        if (this._injectables != null) {
            this.injectValues(ctxt, builder);
        }
        if (this._unwrappedPropertyHandler != null) {
            if (p.hasToken(JsonToken.START_OBJECT)) {
                p.nextToken();
            }
            final TokenBuffer tokens = new TokenBuffer(p, ctxt);
            tokens.writeStartObject();
            return this.deserializeWithUnwrapped(p, ctxt, builder, tokens);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(p, ctxt, builder);
        }
        if (this._needViewProcesing) {
            final Class<?> view = ctxt.getActiveView();
            if (view != null) {
                return this.deserializeWithView(p, ctxt, builder, view);
            }
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        while (t == JsonToken.FIELD_NAME) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, builder, propName, ctxt);
                }
            }
            else {
                this.handleUnknownVanilla(p, ctxt, this.handledType(), propName);
            }
            t = p.nextToken();
        }
        return builder;
    }
    
    protected final Object deserializeWithView(final JsonParser p, final DeserializationContext ctxt, Object bean, final Class<?> activeView) throws IOException {
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (!prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else {
                this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
        }
        return bean;
    }
    
    protected Object deserializeWithUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
        }
        final TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(p, ctxt, bean, propName);
            }
            else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            p.nextToken();
        }
        tokens.writeEndObject();
        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
    }
    
    protected Object deserializeUsingPropertyBasedWithUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Object builder = null;
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                    t = p.nextToken();
                    try {
                        builder = creator.build(ctxt, buffer);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                        continue;
                    }
                    if (builder.getClass() != this._beanType.getRawClass()) {
                        return this.handlePolymorphic(p, ctxt, builder, tokens);
                    }
                    return this.deserializeWithUnwrapped(p, ctxt, builder, tokens);
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
                }
                else {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(p);
                    if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                    }
                }
            }
        }
        tokens.writeEndObject();
        if (builder == null) {
            try {
                builder = creator.build(ctxt, buffer);
            }
            catch (Exception e2) {
                return this.wrapInstantiationProblem(e2, ctxt);
            }
        }
        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
    }
    
    protected Object deserializeWithUnwrapped(final JsonParser p, final DeserializationContext ctxt, Object builder, final TokenBuffer tokens) throws IOException {
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(p, ctxt, builder, propName);
            }
            else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
                if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(p, ctxt, builder, propName);
                }
            }
        }
        tokens.writeEndObject();
        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        return this.deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser p, final DeserializationContext ctxt, Object bean) throws IOException {
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        final ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            t = p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (t.isScalarValue()) {
                    ext.handleTypePropertyValue(p, ctxt, propName, bean);
                }
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(p, ctxt, bean, propName);
            }
            else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
                else {
                    this.handleUnknownProperty(p, ctxt, bean, propName);
                }
            }
        }
        return ext.complete(p, ctxt, bean);
    }
    
    protected Object deserializeUsingPropertyBasedWithExternalTypeId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JavaType t = this._targetType;
        return ctxt.reportBadDefinition(t, String.format("Deserialization (of %s) with Builder, External type id, @JsonCreator not yet implemented", t));
    }
}
