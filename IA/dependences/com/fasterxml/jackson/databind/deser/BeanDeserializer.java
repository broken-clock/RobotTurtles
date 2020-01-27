// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;

public class BeanDeserializer extends BeanDeserializerBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient Exception _nullFromCreator;
    private transient volatile NameTransformer _currentlyTransforming;
    
    public BeanDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final HashSet<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src) {
        super(src, src._ignoreAllUnknown);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src, final boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src, final NameTransformer unwrapper) {
        super(src, unwrapper);
    }
    
    public BeanDeserializer(final BeanDeserializerBase src, final ObjectIdReader oir) {
        super(src, oir);
    }
    
    public BeanDeserializer(final BeanDeserializerBase src, final Set<String> ignorableProps) {
        super(src, ignorableProps);
    }
    
    public BeanDeserializer(final BeanDeserializerBase src, final BeanPropertyMap props) {
        super(src, props);
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer transformer) {
        if (this.getClass() != BeanDeserializer.class) {
            return this;
        }
        if (this._currentlyTransforming == transformer) {
            return this;
        }
        this._currentlyTransforming = transformer;
        try {
            return new BeanDeserializer(this, transformer);
        }
        finally {
            this._currentlyTransforming = null;
        }
    }
    
    @Override
    public BeanDeserializer withObjectIdReader(final ObjectIdReader oir) {
        return new BeanDeserializer(this, oir);
    }
    
    @Override
    public BeanDeserializer withIgnorableProperties(final Set<String> ignorableProps) {
        return new BeanDeserializer(this, ignorableProps);
    }
    
    @Override
    public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
        return new BeanDeserializer(this, props);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        final SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
        return new BeanAsArrayDeserializer(this, props);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartObjectToken()) {
            return this._deserializeOther(p, ctxt, p.getCurrentToken());
        }
        if (this._vanillaProcessing) {
            return this.vanillaDeserialize(p, ctxt, p.nextToken());
        }
        p.nextToken();
        if (this._objectIdReader != null) {
            return this.deserializeWithObjectId(p, ctxt);
        }
        return this.deserializeFromObject(p, ctxt);
    }
    
    protected final Object _deserializeOther(final JsonParser p, final DeserializationContext ctxt, final JsonToken t) throws IOException {
        if (t != null) {
            switch (t) {
                case VALUE_STRING: {
                    return this.deserializeFromString(p, ctxt);
                }
                case VALUE_NUMBER_INT: {
                    return this.deserializeFromNumber(p, ctxt);
                }
                case VALUE_NUMBER_FLOAT: {
                    return this.deserializeFromDouble(p, ctxt);
                }
                case VALUE_EMBEDDED_OBJECT: {
                    return this.deserializeFromEmbedded(p, ctxt);
                }
                case VALUE_TRUE:
                case VALUE_FALSE: {
                    return this.deserializeFromBoolean(p, ctxt);
                }
                case VALUE_NULL: {
                    return this.deserializeFromNull(p, ctxt);
                }
                case START_ARRAY: {
                    return this.deserializeFromArray(p, ctxt);
                }
                case FIELD_NAME:
                case END_OBJECT: {
                    if (this._vanillaProcessing) {
                        return this.vanillaDeserialize(p, ctxt, t);
                    }
                    if (this._objectIdReader != null) {
                        return this.deserializeWithObjectId(p, ctxt);
                    }
                    return this.deserializeFromObject(p, ctxt);
                }
            }
        }
        return ctxt.handleUnexpectedToken(this.handledType(), p);
    }
    
    @Deprecated
    protected Object _missingToken(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        throw ctxt.endOfInputException(this.handledType());
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(p, ctxt, bean);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(p, ctxt, bean);
        }
        String propName;
        if (p.isExpectedStartObjectToken()) {
            propName = p.nextFieldName();
            if (propName == null) {
                return bean;
            }
        }
        else {
            if (!p.hasTokenId(5)) {
                return bean;
            }
            propName = p.getCurrentName();
        }
        if (this._needViewProcesing) {
            final Class<?> view = ctxt.getActiveView();
            if (view != null) {
                return this.deserializeWithView(p, ctxt, bean, view);
            }
        }
        do {
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, propName, ctxt);
                }
            }
            else {
                this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
        } while ((propName = p.nextFieldName()) != null);
        return bean;
    }
    
    private final Object vanillaDeserialize(final JsonParser p, final DeserializationContext ctxt, final JsonToken t) throws IOException {
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (p.hasTokenId(5)) {
            String propName = p.getCurrentName();
            do {
                p.nextToken();
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
                else {
                    this.handleUnknownVanilla(p, ctxt, bean, propName);
                }
            } while ((propName = p.nextFieldName()) != null);
        }
        return bean;
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
            return this.deserializeFromObjectId(p, ctxt);
        }
        if (!this._nonStandardCreation) {
            final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
            p.setCurrentValue(bean);
            if (p.canReadObjectId()) {
                final Object id = p.getObjectId();
                if (id != null) {
                    this._handleTypedObjectId(p, ctxt, bean, id);
                }
            }
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            if (this._needViewProcesing) {
                final Class<?> view = ctxt.getActiveView();
                if (view != null) {
                    return this.deserializeWithView(p, ctxt, bean, view);
                }
            }
            if (p.hasTokenId(5)) {
                String propName = p.getCurrentName();
                do {
                    p.nextToken();
                    final SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null) {
                        try {
                            prop.deserializeAndSet(p, ctxt, bean);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, bean, propName, ctxt);
                        }
                    }
                    else {
                        this.handleUnknownVanilla(p, ctxt, bean, propName);
                    }
                } while ((propName = p.nextFieldName()) != null);
            }
            return bean;
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(p, ctxt);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(p, ctxt);
        }
        final Object bean = this.deserializeFromObjectUsingNonDefault(p, ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        return bean;
    }
    
    @Override
    protected Object _deserializeUsingPropertyBased(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer unknown = null;
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        JsonToken t = p.getCurrentToken();
        List<BeanReferring> referrings = null;
        while (t == JsonToken.FIELD_NAME) {
            final String propName = p.getCurrentName();
            p.nextToken();
            if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (creatorProp != null) {
                    if (activeView != null && !creatorProp.visibleInView(activeView)) {
                        p.skipChildren();
                    }
                    else {
                        final Object value = this._deserializeWithErrorWrapping(p, ctxt, creatorProp);
                        if (buffer.assignParameter(creatorProp, value)) {
                            p.nextToken();
                            Object bean;
                            try {
                                bean = creator.build(ctxt, buffer);
                            }
                            catch (Exception e) {
                                bean = this.wrapInstantiationProblem(e, ctxt);
                            }
                            if (bean == null) {
                                return ctxt.handleInstantiationProblem(this.handledType(), null, this._creatorReturnedNullException());
                            }
                            p.setCurrentValue(bean);
                            if (bean.getClass() != this._beanType.getRawClass()) {
                                return this.handlePolymorphic(p, ctxt, bean, unknown);
                            }
                            if (unknown != null) {
                                bean = this.handleUnknownProperties(ctxt, bean, unknown);
                            }
                            return this.deserialize(p, ctxt, bean);
                        }
                    }
                }
                else {
                    final SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null) {
                        try {
                            buffer.bufferProperty(prop, this._deserializeWithErrorWrapping(p, ctxt, prop));
                        }
                        catch (UnresolvedForwardReference reference) {
                            final BeanReferring referring = this.handleUnresolvedReference(ctxt, prop, buffer, reference);
                            if (referrings == null) {
                                referrings = new ArrayList<BeanReferring>();
                            }
                            referrings.add(referring);
                        }
                    }
                    else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
                    }
                    else if (this._anySetter != null) {
                        try {
                            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                        }
                        catch (Exception e2) {
                            this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                        }
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
            t = p.nextToken();
        }
        Object bean2;
        try {
            bean2 = creator.build(ctxt, buffer);
        }
        catch (Exception e3) {
            this.wrapInstantiationProblem(e3, ctxt);
            bean2 = null;
        }
        if (referrings != null) {
            for (final BeanReferring referring2 : referrings) {
                referring2.setBean(bean2);
            }
        }
        if (unknown == null) {
            return bean2;
        }
        if (bean2.getClass() != this._beanType.getRawClass()) {
            return this.handlePolymorphic(null, ctxt, bean2, unknown);
        }
        return this.handleUnknownProperties(ctxt, bean2, unknown);
    }
    
    private BeanReferring handleUnresolvedReference(final DeserializationContext ctxt, final SettableBeanProperty prop, final PropertyValueBuffer buffer, final UnresolvedForwardReference reference) throws JsonMappingException {
        final BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
        reference.getRoid().appendReferring(referring);
        return referring;
    }
    
    protected final Object _deserializeWithErrorWrapping(final JsonParser p, final DeserializationContext ctxt, final SettableBeanProperty prop) throws IOException {
        try {
            return prop.deserialize(p, ctxt);
        }
        catch (Exception e) {
            this.wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
            return null;
        }
    }
    
    protected Object deserializeFromNull(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.requiresCustomCodec()) {
            final TokenBuffer tb = new TokenBuffer(p, ctxt);
            tb.writeEndObject();
            final JsonParser p2 = tb.asParser(p);
            p2.nextToken();
            final Object ob = this._vanillaProcessing ? this.vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : this.deserializeFromObject(p2, ctxt);
            p2.close();
            return ob;
        }
        return ctxt.handleUnexpectedToken(this.handledType(), p);
    }
    
    protected final Object deserializeWithView(final JsonParser p, final DeserializationContext ctxt, final Object bean, final Class<?> activeView) throws IOException {
        if (p.hasTokenId(5)) {
            String propName = p.getCurrentName();
            do {
                p.nextToken();
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    if (!prop.visibleInView(activeView)) {
                        p.skipChildren();
                    }
                    else {
                        try {
                            prop.deserializeAndSet(p, ctxt, bean);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, bean, propName, ctxt);
                        }
                    }
                }
                else {
                    this.handleUnknownVanilla(p, ctxt, bean, propName);
                }
            } while ((propName = p.nextFieldName()) != null);
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
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        for (String propName = p.hasTokenId(5) ? p.getCurrentName() : null; propName != null; propName = p.nextFieldName()) {
            p.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(p, ctxt, bean, propName);
            }
            else if (this._anySetter == null) {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
            }
            else {
                final TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
                tokens.writeFieldName(propName);
                tokens.append(b2);
                try {
                    this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
                }
                catch (Exception e2) {
                    this.wrapAndThrow(e2, bean, propName, ctxt);
                }
            }
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }
    
    protected Object deserializeWithUnwrapped(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        final TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (t == JsonToken.FIELD_NAME) {
            final String propName = p.getCurrentName();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(p, ctxt, bean, propName);
            }
            else if (this._anySetter == null) {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
            }
            else {
                final TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
                tokens.writeFieldName(propName);
                tokens.append(b2);
                try {
                    this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
                }
                catch (Exception e2) {
                    this.wrapAndThrow(e2, bean, propName, ctxt);
                }
            }
            t = p.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }
    
    protected Object deserializeUsingPropertyBasedWithUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
                    t = p.nextToken();
                    Object bean;
                    try {
                        bean = creator.build(ctxt, buffer);
                    }
                    catch (Exception e) {
                        bean = this.wrapInstantiationProblem(e, ctxt);
                    }
                    p.setCurrentValue(bean);
                    while (t == JsonToken.FIELD_NAME) {
                        tokens.copyCurrentStructure(p);
                        t = p.nextToken();
                    }
                    if (t != JsonToken.END_OBJECT) {
                        ctxt.reportWrongTokenException(this, JsonToken.END_OBJECT, "Attempted to unwrap '%s' value", this.handledType().getName());
                    }
                    tokens.writeEndObject();
                    if (bean.getClass() != this._beanType.getRawClass()) {
                        ctxt.reportInputMismatch(creatorProp, "Cannot create polymorphic instances with unwrapped values", new Object[0]);
                        return null;
                    }
                    return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, this._deserializeWithErrorWrapping(p, ctxt, prop));
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
                }
                else if (this._anySetter == null) {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(p);
                }
                else {
                    final TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
                    tokens.writeFieldName(propName);
                    tokens.append(b2);
                    try {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(b2.asParserOnFirstToken(), ctxt));
                    }
                    catch (Exception e2) {
                        this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                    }
                }
            }
        }
        Object bean2;
        try {
            bean2 = creator.build(ctxt, buffer);
        }
        catch (Exception e3) {
            this.wrapInstantiationProblem(e3, ctxt);
            return null;
        }
        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean2, tokens);
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        return this.deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
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
                        prop.deserializeAndSet(p, ctxt, bean);
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
        final ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        final TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.getCurrentName();
            p.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
                    if (buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
                        t = p.nextToken();
                        Object bean;
                        try {
                            bean = creator.build(ctxt, buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                            continue;
                        }
                        while (t == JsonToken.FIELD_NAME) {
                            p.nextToken();
                            tokens.copyCurrentStructure(p);
                            t = p.nextToken();
                        }
                        if (bean.getClass() != this._beanType.getRawClass()) {
                            return ctxt.reportBadDefinition(this._beanType, String.format("Cannot create polymorphic instances with external type ids (%s -> %s)", this._beanType, bean.getClass()));
                        }
                        return ext.complete(p, ctxt, bean);
                    }
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                }
                else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
                    if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
                    }
                    else if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                    }
                }
            }
        }
        tokens.writeEndObject();
        try {
            return ext.complete(p, ctxt, buffer, creator);
        }
        catch (Exception e2) {
            return this.wrapInstantiationProblem(e2, ctxt);
        }
    }
    
    protected Exception _creatorReturnedNullException() {
        if (this._nullFromCreator == null) {
            this._nullFromCreator = new NullPointerException("JSON Creator returned null");
        }
        return this._nullFromCreator;
    }
    
    static class BeanReferring extends ReadableObjectId.Referring
    {
        private final DeserializationContext _context;
        private final SettableBeanProperty _prop;
        private Object _bean;
        
        BeanReferring(final DeserializationContext ctxt, final UnresolvedForwardReference ref, final JavaType valueType, final PropertyValueBuffer buffer, final SettableBeanProperty prop) {
            super(ref, valueType);
            this._context = ctxt;
            this._prop = prop;
        }
        
        public void setBean(final Object bean) {
            this._bean = bean;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            if (this._bean == null) {
                this._context.reportInputMismatch(this._prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", this._prop.getName(), this._prop.getDeclaringClass().getName());
            }
            this._prop.set(this._bean, value);
        }
    }
}
