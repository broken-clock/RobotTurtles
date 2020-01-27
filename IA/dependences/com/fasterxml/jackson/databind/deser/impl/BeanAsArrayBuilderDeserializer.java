// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;

public class BeanAsArrayBuilderDeserializer extends BeanDeserializerBase
{
    private static final long serialVersionUID = 1L;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;
    protected final AnnotatedMethod _buildMethod;
    protected final JavaType _targetType;
    
    public BeanAsArrayBuilderDeserializer(final BeanDeserializerBase delegate, final JavaType targetType, final SettableBeanProperty[] ordered, final AnnotatedMethod buildMethod) {
        super(delegate);
        this._delegate = delegate;
        this._targetType = targetType;
        this._orderedProperties = ordered;
        this._buildMethod = buildMethod;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }
    
    @Override
    public BeanDeserializerBase withObjectIdReader(final ObjectIdReader oir) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withObjectIdReader(oir), this._targetType, this._orderedProperties, this._buildMethod);
    }
    
    @Override
    public BeanDeserializerBase withIgnorableProperties(final Set<String> ignorableProps) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withIgnorableProperties(ignorableProps), this._targetType, this._orderedProperties, this._buildMethod);
    }
    
    @Override
    public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withBeanProperties(props), this._targetType, this._orderedProperties, this._buildMethod);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        return this;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    protected final Object finishBuild(final DeserializationContext ctxt, final Object builder) throws IOException {
        try {
            return this._buildMethod.getMember().invoke(builder, (Object[])null);
        }
        catch (Exception e) {
            return this.wrapInstantiationProblem(e, ctxt);
        }
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.finishBuild(ctxt, this._deserializeFromNonArray(p, ctxt));
        }
        if (!this._vanillaProcessing) {
            return this.finishBuild(ctxt, this._deserializeNonVanilla(p, ctxt));
        }
        Object builder = this._valueInstantiator.createUsingDefault(ctxt);
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportInputMismatch(this.handledType(), "Unexpected JSON values; expected at most %d properties (in JSON Array)", propCount);
                }
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    p.skipChildren();
                }
                return this.finishBuild(ctxt, builder);
            }
            final SettableBeanProperty prop = props[i];
            if (prop != null) {
                try {
                    builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                }
            }
            else {
                p.skipChildren();
            }
            ++i;
        }
        return this.finishBuild(ctxt, builder);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object value) throws IOException {
        return this._delegate.deserialize(p, ctxt, value);
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this._deserializeFromNonArray(p, ctxt);
    }
    
    protected Object _deserializeNonVanilla(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._nonStandardCreation) {
            return this.deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        final Object builder = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, builder);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON value(s); expected at most %d properties (in JSON Array)", propCount);
                }
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    p.skipChildren();
                }
                return builder;
            }
            final SettableBeanProperty prop = props[i];
            ++i;
            Label_0149: {
                if (prop != null) {
                    if (activeView != null) {
                        if (!prop.visibleInView(activeView)) {
                            break Label_0149;
                        }
                    }
                    try {
                        prop.deserializeSetAndReturn(p, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                    continue;
                }
            }
            p.skipChildren();
        }
        return builder;
    }
    
    @Override
    protected final Object _deserializeUsingPropertyBased(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final SettableBeanProperty[] props = this._orderedProperties;
        final int propCount = props.length;
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        int i = 0;
        Object builder = null;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            final SettableBeanProperty prop = (i < propCount) ? props[i] : null;
            Label_0307: {
                if (prop == null) {
                    p.skipChildren();
                }
                else if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else if (builder != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                }
                else {
                    final String propName = prop.getName();
                    final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                    if (creatorProp != null) {
                        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                            try {
                                builder = creator.build(ctxt, buffer);
                            }
                            catch (Exception e2) {
                                this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                                break Label_0307;
                            }
                            if (builder.getClass() != this._beanType.getRawClass()) {
                                return ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", this._beanType.getRawClass().getName(), builder.getClass().getName()));
                            }
                        }
                    }
                    else if (!buffer.readIdProperty(propName)) {
                        buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                    }
                }
            }
            ++i;
        }
        if (builder == null) {
            try {
                builder = creator.build(ctxt, buffer);
            }
            catch (Exception e3) {
                return this.wrapInstantiationProblem(e3, ctxt);
            }
        }
        return builder;
    }
    
    protected Object _deserializeFromNonArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return ctxt.handleUnexpectedToken(this.handledType(), p.getCurrentToken(), p, "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array", this._beanType.getRawClass().getName(), p.getCurrentToken());
    }
}
