// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;

public class JSONPObject implements JsonSerializable
{
    protected final String _function;
    protected final Object _value;
    protected final JavaType _serializationType;
    
    public JSONPObject(final String function, final Object value) {
        this(function, value, null);
    }
    
    public JSONPObject(final String function, final Object value, final JavaType asType) {
        this._function = function;
        this._value = value;
        this._serializationType = asType;
    }
    
    @Override
    public void serializeWithType(final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        this.serialize(gen, provider);
    }
    
    @Override
    public void serialize(final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeRaw(this._function);
        gen.writeRaw('(');
        if (this._value == null) {
            provider.defaultSerializeNull(gen);
        }
        else {
            final boolean override = gen.getCharacterEscapes() == null;
            if (override) {
                gen.setCharacterEscapes(JsonpCharacterEscapes.instance());
            }
            try {
                if (this._serializationType != null) {
                    provider.findTypedValueSerializer(this._serializationType, true, null).serialize(this._value, gen, provider);
                }
                else {
                    provider.findTypedValueSerializer(this._value.getClass(), true, null).serialize(this._value, gen, provider);
                }
            }
            finally {
                if (override) {
                    gen.setCharacterEscapes(null);
                }
            }
        }
        gen.writeRaw(')');
    }
    
    public String getFunction() {
        return this._function;
    }
    
    public Object getValue() {
        return this._value;
    }
    
    public JavaType getSerializationType() {
        return this._serializationType;
    }
}
