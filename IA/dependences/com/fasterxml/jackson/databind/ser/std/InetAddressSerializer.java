// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.net.InetAddress;

public class InetAddressSerializer extends StdScalarSerializer<InetAddress> implements ContextualSerializer
{
    protected final boolean _asNumeric;
    
    public InetAddressSerializer() {
        this(false);
    }
    
    public InetAddressSerializer(final boolean asNumeric) {
        super(InetAddress.class);
        this._asNumeric = asNumeric;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
        boolean asNumeric = false;
        if (format != null) {
            final JsonFormat.Shape shape = format.getShape();
            if (shape.isNumeric() || shape == JsonFormat.Shape.ARRAY) {
                asNumeric = true;
            }
        }
        if (asNumeric != this._asNumeric) {
            return new InetAddressSerializer(asNumeric);
        }
        return this;
    }
    
    @Override
    public void serialize(final InetAddress value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        String str;
        if (this._asNumeric) {
            str = value.getHostAddress();
        }
        else {
            str = value.toString().trim();
            final int ix = str.indexOf(47);
            if (ix >= 0) {
                if (ix == 0) {
                    str = str.substring(1);
                }
                else {
                    str = str.substring(0, ix);
                }
            }
        }
        g.writeString(str);
    }
    
    @Override
    public void serializeWithType(final InetAddress value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, InetAddress.class, JsonToken.VALUE_STRING));
        this.serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
