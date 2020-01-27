// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Collection;

@JacksonStdImpl
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    
    public StringCollectionDeserializer(final JavaType collectionType, final JsonDeserializer<?> valueDeser, final ValueInstantiator valueInstantiator) {
        this(collectionType, valueInstantiator, null, valueDeser, valueDeser, null);
    }
    
    protected StringCollectionDeserializer(final JavaType collectionType, final ValueInstantiator valueInstantiator, final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(collectionType, nuller, unwrapSingle);
        this._valueDeserializer = (JsonDeserializer<String>)valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = (JsonDeserializer<Object>)delegateDeser;
    }
    
    protected StringCollectionDeserializer withResolved(final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        if (this._unwrapSingle == unwrapSingle && this._nullProvider == nuller && this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser) {
            return this;
        }
        return new StringCollectionDeserializer(this._containerType, this._valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._valueDeserializer == null && this._delegateDeserializer == null;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegate = null;
        if (this._valueInstantiator != null) {
            final AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            if (delegateCreator != null) {
                final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                delegate = this.findDeserializer(ctxt, delegateType, property);
            }
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        final JavaType valueType = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
            if (valueDeser == null) {
                valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
            }
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
        }
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        final NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
        if (this.isDefaultDeserializer(valueDeser)) {
            valueDeser = null;
        }
        return this.withResolved(delegate, valueDeser, nuller, unwrapSingle);
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        final JsonDeserializer<?> deser = this._valueDeserializer;
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection<String>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        final Collection<String> result = (Collection<String>)this._valueInstantiator.createUsingDefault(ctxt);
        return this.deserialize(p, ctxt, result);
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        if (this._valueDeserializer != null) {
            return this.deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
        }
        Label_0034: {
            break Label_0034;
            try {
                while (true) {
                    String value = p.nextTextValue();
                    if (value != null) {
                        result.add(value);
                    }
                    else {
                        final JsonToken t = p.getCurrentToken();
                        if (t == JsonToken.END_ARRAY) {
                            break;
                        }
                        if (t == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                continue;
                            }
                            value = (String)this._nullProvider.getNullValue(ctxt);
                        }
                        else {
                            value = this._parseString(p, ctxt);
                        }
                        result.add(value);
                    }
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
        }
        return result;
    }
    
    private Collection<String> deserializeUsingCustom(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result, final JsonDeserializer<String> deser) throws IOException {
        while (true) {
            String value;
            if (p.nextTextValue() == null) {
                final JsonToken t = p.getCurrentToken();
                if (t == JsonToken.END_ARRAY) {
                    break;
                }
                if (t == JsonToken.VALUE_NULL) {
                    if (this._skipNullValues) {
                        continue;
                    }
                    value = (String)this._nullProvider.getNullValue(ctxt);
                }
                else {
                    value = deser.deserialize(p, ctxt);
                }
            }
            else {
                value = deser.deserialize(p, ctxt);
            }
            result.add(value);
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    private final Collection<String> handleNonArray(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (Collection<String>)ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p);
        }
        final JsonDeserializer<String> valueDes = this._valueDeserializer;
        final JsonToken t = p.getCurrentToken();
        String value;
        if (t == JsonToken.VALUE_NULL) {
            if (this._skipNullValues) {
                return result;
            }
            value = (String)this._nullProvider.getNullValue(ctxt);
        }
        else {
            value = ((valueDes == null) ? this._parseString(p, ctxt) : valueDes.deserialize(p, ctxt));
        }
        result.add(value);
        return result;
    }
}
