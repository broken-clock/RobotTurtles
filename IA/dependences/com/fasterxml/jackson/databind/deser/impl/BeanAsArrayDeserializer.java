// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;

public class BeanAsArrayDeserializer extends BeanDeserializerBase
{
    private static final long serialVersionUID = 1L;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;
    
    public BeanAsArrayDeserializer(final BeanDeserializerBase delegate, final SettableBeanProperty[] ordered) {
        super(delegate);
        this._delegate = delegate;
        this._orderedProperties = ordered;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }
    
    @Override
    public BeanDeserializerBase withObjectIdReader(final ObjectIdReader oir) {
        return new BeanAsArrayDeserializer(this._delegate.withObjectIdReader(oir), this._orderedProperties);
    }
    
    @Override
    public BeanDeserializerBase withIgnorableProperties(final Set<String> ignorableProps) {
        return new BeanAsArrayDeserializer(this._delegate.withIgnorableProperties(ignorableProps), this._orderedProperties);
    }
    
    @Override
    public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
        return new BeanAsArrayDeserializer(this._delegate.withBeanProperties(props), this._orderedProperties);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        return this;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this._deserializeFromNonArray(p, ctxt);
        }
        if (!this._vanillaProcessing) {
            return this._deserializeNonVanilla(p, ctxt);
        }
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", propCount);
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
            final SettableBeanProperty prop = props[i];
            if (prop != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                }
            }
            else {
                p.skipChildren();
            }
            ++i;
        }
        return bean;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        p.setCurrentValue(bean);
        if (!p.isExpectedStartArrayToken()) {
            return this._deserializeFromNonArray(p, ctxt);
        }
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", propCount);
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
            final SettableBeanProperty prop = props[i];
            if (prop != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                }
            }
            else {
                p.skipChildren();
            }
            ++i;
        }
        return bean;
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this._deserializeFromNonArray(p, ctxt);
    }
    
    protected Object _deserializeNonVanilla(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._nonStandardCreation) {
            return this.deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", propCount);
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
            final SettableBeanProperty prop = props[i];
            ++i;
            Label_0153: {
                if (prop != null) {
                    if (activeView != null) {
                        if (!prop.visibleInView(activeView)) {
                            break Label_0153;
                        }
                    }
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                    continue;
                }
            }
            p.skipChildren();
        }
        return bean;
    }
    
    @Override
    protected final Object _deserializeUsingPropertyBased(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final SettableBeanProperty[] props = this._orderedProperties;
        final int propCount = props.length;
        int i = 0;
        Object bean = null;
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            final SettableBeanProperty prop = (i < propCount) ? props[i] : null;
            Label_0314: {
                if (prop == null) {
                    p.skipChildren();
                }
                else if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else if (bean != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                }
                else {
                    final String propName = prop.getName();
                    final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                    if (creatorProp != null) {
                        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                            try {
                                bean = creator.build(ctxt, buffer);
                            }
                            catch (Exception e2) {
                                this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                                break Label_0314;
                            }
                            p.setCurrentValue(bean);
                            if (bean.getClass() != this._beanType.getRawClass()) {
                                ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", this._beanType.getRawClass().getName(), bean.getClass().getName()));
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
        if (bean == null) {
            try {
                bean = creator.build(ctxt, buffer);
            }
            catch (Exception e3) {
                return this.wrapInstantiationProblem(e3, ctxt);
            }
        }
        return bean;
    }
    
    protected Object _deserializeFromNonArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return ctxt.handleUnexpectedToken(this.handledType(), p.getCurrentToken(), p, "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array", this._beanType.getRawClass().getName(), p.getCurrentToken());
    }
}
