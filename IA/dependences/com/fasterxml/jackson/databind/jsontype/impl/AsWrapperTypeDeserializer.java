// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class AsWrapperTypeDeserializer extends TypeDeserializerBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public AsWrapperTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final JavaType defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }
    
    protected AsWrapperTypeDeserializer(final AsWrapperTypeDeserializer src, final BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        return (prop == this._property) ? this : new AsWrapperTypeDeserializer(this, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_OBJECT;
    }
    
    @Override
    public Object deserializeTypedFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromScalar(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromAny(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    protected Object _deserialize(JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.canReadTypeId()) {
            final Object typeId = p.getTypeId();
            if (typeId != null) {
                return this._deserializeWithNativeTypeId(p, ctxt, typeId);
            }
        }
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            if (p.nextToken() != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this.baseType(), JsonToken.FIELD_NAME, "need JSON String that contains type id (for subtype of " + this.baseTypeName() + ")", new Object[0]);
            }
        }
        else if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.START_OBJECT, "need JSON Object to contain As.WRAPPER_OBJECT type information for class " + this.baseTypeName(), new Object[0]);
        }
        final String typeId2 = p.getText();
        final JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId2);
        p.nextToken();
        if (this._typeIdVisible && p.getCurrentToken() == JsonToken.START_OBJECT) {
            final TokenBuffer tb = new TokenBuffer(null, false);
            tb.writeStartObject();
            tb.writeFieldName(this._typePropertyName);
            tb.writeString(typeId2);
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
            p.nextToken();
        }
        final Object value = deser.deserialize(p, ctxt);
        if (p.nextToken() != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.END_OBJECT, "expected closing END_OBJECT after type information and deserialized value", new Object[0]);
        }
        return value;
    }
}
