// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;

public abstract class TypeSerializer
{
    public abstract TypeSerializer forProperty(final BeanProperty p0);
    
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    public abstract String getPropertyName();
    
    public abstract TypeIdResolver getTypeIdResolver();
    
    public WritableTypeId typeId(final Object value, final JsonToken valueShape) {
        final WritableTypeId typeIdDef = new WritableTypeId(value, valueShape);
        switch (this.getTypeInclusion()) {
            case EXISTING_PROPERTY: {
                typeIdDef.include = WritableTypeId.Inclusion.PAYLOAD_PROPERTY;
                typeIdDef.asProperty = this.getPropertyName();
                break;
            }
            case EXTERNAL_PROPERTY: {
                typeIdDef.include = WritableTypeId.Inclusion.PARENT_PROPERTY;
                typeIdDef.asProperty = this.getPropertyName();
                break;
            }
            case PROPERTY: {
                typeIdDef.include = WritableTypeId.Inclusion.METADATA_PROPERTY;
                typeIdDef.asProperty = this.getPropertyName();
                break;
            }
            case WRAPPER_ARRAY: {
                typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_ARRAY;
                break;
            }
            case WRAPPER_OBJECT: {
                typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_OBJECT;
                break;
            }
            default: {
                VersionUtil.throwInternal();
                break;
            }
        }
        return typeIdDef;
    }
    
    public WritableTypeId typeId(final Object value, final JsonToken valueShape, final Object id) {
        final WritableTypeId typeId = this.typeId(value, valueShape);
        typeId.id = id;
        return typeId;
    }
    
    public WritableTypeId typeId(final Object value, final Class<?> typeForId, final JsonToken valueShape) {
        final WritableTypeId typeId = this.typeId(value, valueShape);
        typeId.forValueType = typeForId;
        return typeId;
    }
    
    public abstract WritableTypeId writeTypePrefix(final JsonGenerator p0, final WritableTypeId p1) throws IOException;
    
    public abstract WritableTypeId writeTypeSuffix(final JsonGenerator p0, final WritableTypeId p1) throws IOException;
    
    @Deprecated
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator g) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.VALUE_STRING));
    }
    
    @Deprecated
    public void writeTypePrefixForObject(final Object value, final JsonGenerator g) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.START_OBJECT));
    }
    
    @Deprecated
    public void writeTypePrefixForArray(final Object value, final JsonGenerator g) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.START_ARRAY));
    }
    
    @Deprecated
    public void writeTypeSuffixForScalar(final Object value, final JsonGenerator g) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.VALUE_STRING));
    }
    
    @Deprecated
    public void writeTypeSuffixForObject(final Object value, final JsonGenerator g) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_OBJECT));
    }
    
    @Deprecated
    public void writeTypeSuffixForArray(final Object value, final JsonGenerator g) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_ARRAY));
    }
    
    @Deprecated
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator g, final Class<?> type) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, type, JsonToken.VALUE_STRING));
    }
    
    @Deprecated
    public void writeTypePrefixForObject(final Object value, final JsonGenerator g, final Class<?> type) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, type, JsonToken.START_OBJECT));
    }
    
    @Deprecated
    public void writeTypePrefixForArray(final Object value, final JsonGenerator g, final Class<?> type) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, type, JsonToken.START_ARRAY));
    }
    
    @Deprecated
    public void writeCustomTypePrefixForScalar(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.VALUE_STRING, typeId));
    }
    
    @Deprecated
    public void writeCustomTypePrefixForObject(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.START_OBJECT, typeId));
    }
    
    @Deprecated
    public void writeCustomTypePrefixForArray(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this.writeTypePrefix(g, this.typeId(value, JsonToken.START_ARRAY, typeId));
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForScalar(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.VALUE_STRING, typeId));
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForObject(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_OBJECT, typeId));
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForArray(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_ARRAY, typeId));
    }
    
    protected final void _writeLegacySuffix(final JsonGenerator g, final WritableTypeId typeId) throws IOException {
        typeId.wrapperWritten = !g.canWriteTypeId();
        this.writeTypeSuffix(g, typeId);
    }
}
