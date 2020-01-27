// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Collection;

@JacksonStdImpl
public class CollectionDeserializer extends ContainerDeserializerBase<Collection<Object>> implements ContextualDeserializer
{
    private static final long serialVersionUID = -1L;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    
    public CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator) {
        this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, null, null, null);
    }
    
    protected CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator, final JsonDeserializer<Object> delegateDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(collectionType, nuller, unwrapSingle);
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = delegateDeser;
    }
    
    protected CollectionDeserializer(final CollectionDeserializer src) {
        super(src);
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
    }
    
    protected CollectionDeserializer withResolved(final JsonDeserializer<?> dd, final JsonDeserializer<?> vd, final TypeDeserializer vtd, final NullValueProvider nuller, final Boolean unwrapSingle) {
        return new CollectionDeserializer(this._containerType, (JsonDeserializer<Object>)vd, vtd, this._valueInstantiator, (JsonDeserializer<Object>)dd, nuller, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._valueDeserializer == null && this._valueTypeDeserializer == null && this._delegateDeserializer == null;
    }
    
    @Override
    public CollectionDeserializer createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegateDeser = null;
        if (this._valueInstantiator != null) {
            if (this._valueInstantiator.canCreateUsingDelegate()) {
                final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                delegateDeser = this.findDeserializer(ctxt, delegateType, property);
            }
            else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
                final JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                delegateDeser = this.findDeserializer(ctxt, delegateType, property);
            }
        }
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
        final JavaType vt = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
        if (valueTypeDeser != null) {
            valueTypeDeser = valueTypeDeser.forProperty(property);
        }
        final NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
        if (unwrapSingle != this._unwrapSingle || nuller != this._nullProvider || delegateDeser != this._delegateDeserializer || valueDeser != this._valueDeserializer || valueTypeDeser != this._valueTypeDeserializer) {
            return this.withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
        }
        return this;
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public Collection<Object> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection<Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            final String str = p.getText();
            if (str.length() == 0) {
                return (Collection<Object>)this._valueInstantiator.createFromString(ctxt, str);
            }
        }
        return this.deserialize(p, ctxt, this.createDefaultInstance(ctxt));
    }
    
    protected Collection<Object> createDefaultInstance(final DeserializationContext ctxt) throws IOException {
        return (Collection<Object>)this._valueInstantiator.createUsingDefault(ctxt);
    }
    
    @Override
    public Collection<Object> deserialize(final JsonParser p, final DeserializationContext ctxt, final Collection<Object> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        p.setCurrentValue(result);
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        if (valueDes.getObjectIdReader() != null) {
            return this._deserializeWithObjectId(p, ctxt, result);
        }
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            try {
                Object value;
                if (t == JsonToken.VALUE_NULL) {
                    if (this._skipNullValues) {
                        continue;
                    }
                    value = this._nullProvider.getNullValue(ctxt);
                }
                else if (typeDeser == null) {
                    value = valueDes.deserialize(p, ctxt);
                }
                else {
                    value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                }
                result.add(value);
                continue;
            }
            catch (Exception e) {
                final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
                if (!wrap) {
                    ClassUtil.throwIfRTE(e);
                }
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
            break;
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    protected final Collection<Object> handleNonArray(final JsonParser p, final DeserializationContext ctxt, final Collection<Object> result) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (Collection<Object>)ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p);
        }
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        final JsonToken t = p.getCurrentToken();
        Object value;
        try {
            if (t == JsonToken.VALUE_NULL) {
                if (this._skipNullValues) {
                    return result;
                }
                value = this._nullProvider.getNullValue(ctxt);
            }
            else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            }
            else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
        }
        catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, Object.class, result.size());
        }
        result.add(value);
        return result;
    }
    
    protected Collection<Object> _deserializeWithObjectId(final JsonParser p, final DeserializationContext ctxt, final Collection<Object> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        p.setCurrentValue(result);
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        final CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            try {
                Object value;
                if (t == JsonToken.VALUE_NULL) {
                    if (this._skipNullValues) {
                        continue;
                    }
                    value = this._nullProvider.getNullValue(ctxt);
                }
                else if (typeDeser == null) {
                    value = valueDes.deserialize(p, ctxt);
                }
                else {
                    value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                }
                referringAccumulator.add(value);
                continue;
            }
            catch (UnresolvedForwardReference reference) {
                final ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
                reference.getRoid().appendReferring(ref);
                continue;
            }
            catch (Exception e) {
                final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
                if (!wrap) {
                    ClassUtil.throwIfRTE(e);
                }
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
            break;
        }
        return result;
    }
    
    public static class CollectionReferringAccumulator
    {
        private final Class<?> _elementType;
        private final Collection<Object> _result;
        private List<CollectionReferring> _accumulator;
        
        public CollectionReferringAccumulator(final Class<?> elementType, final Collection<Object> result) {
            this._accumulator = new ArrayList<CollectionReferring>();
            this._elementType = elementType;
            this._result = result;
        }
        
        public void add(final Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.add(value);
            }
            else {
                final CollectionReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.add(value);
            }
        }
        
        public ReadableObjectId.Referring handleUnresolvedReference(final UnresolvedForwardReference reference) {
            final CollectionReferring id = new CollectionReferring(this, reference, this._elementType);
            this._accumulator.add(id);
            return id;
        }
        
        public void resolveForwardReference(final Object id, final Object value) throws IOException {
            final Iterator<CollectionReferring> iterator = this._accumulator.iterator();
            Collection<Object> previous = this._result;
            while (iterator.hasNext()) {
                final CollectionReferring ref = iterator.next();
                if (ref.hasId(id)) {
                    iterator.remove();
                    previous.add(value);
                    previous.addAll(ref.next);
                    return;
                }
                previous = ref.next;
            }
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
        }
    }
    
    private static final class CollectionReferring extends ReadableObjectId.Referring
    {
        private final CollectionReferringAccumulator _parent;
        public final List<Object> next;
        
        CollectionReferring(final CollectionReferringAccumulator parent, final UnresolvedForwardReference reference, final Class<?> contentType) {
            super(reference, contentType);
            this.next = new ArrayList<Object>();
            this._parent = parent;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
