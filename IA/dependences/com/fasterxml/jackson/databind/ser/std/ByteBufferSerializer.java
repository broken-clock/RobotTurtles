// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import java.io.InputStream;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.nio.ByteBuffer;

public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer>
{
    public ByteBufferSerializer() {
        super(ByteBuffer.class);
    }
    
    @Override
    public void serialize(final ByteBuffer bbuf, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (bbuf.hasArray()) {
            gen.writeBinary(bbuf.array(), bbuf.arrayOffset(), bbuf.limit());
            return;
        }
        final ByteBuffer copy = bbuf.asReadOnlyBuffer();
        if (copy.position() > 0) {
            copy.rewind();
        }
        final InputStream in = new ByteBufferBackedInputStream(copy);
        gen.writeBinary(in, copy.remaining());
        in.close();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }
}
