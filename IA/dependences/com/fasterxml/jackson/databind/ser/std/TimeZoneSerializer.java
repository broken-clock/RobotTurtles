// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.TimeZone;

public class TimeZoneSerializer extends StdScalarSerializer<TimeZone>
{
    public TimeZoneSerializer() {
        super(TimeZone.class);
    }
    
    @Override
    public void serialize(final TimeZone value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.writeString(value.getID());
    }
    
    @Override
    public void serializeWithType(final TimeZone value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, TimeZone.class, JsonToken.VALUE_STRING));
        this.serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
