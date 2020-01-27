// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.UUID;

public class UUIDSerializer extends StdScalarSerializer<UUID>
{
    static final char[] HEX_CHARS;
    
    public UUIDSerializer() {
        super(UUID.class);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final UUID value) {
        return value.getLeastSignificantBits() == 0L && value.getMostSignificantBits() == 0L;
    }
    
    @Override
    public void serialize(final UUID value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (gen.canWriteBinaryNatively() && !(gen instanceof TokenBuffer)) {
            gen.writeBinary(_asBytes(value));
            return;
        }
        final char[] ch = new char[36];
        final long msb = value.getMostSignificantBits();
        _appendInt((int)(msb >> 32), ch, 0);
        ch[8] = '-';
        final int i = (int)msb;
        _appendShort(i >>> 16, ch, 9);
        ch[13] = '-';
        _appendShort(i, ch, 14);
        ch[18] = '-';
        final long lsb = value.getLeastSignificantBits();
        _appendShort((int)(lsb >>> 48), ch, 19);
        ch[23] = '-';
        _appendShort((int)(lsb >>> 32), ch, 24);
        _appendInt((int)lsb, ch, 28);
        gen.writeString(ch, 0, 36);
    }
    
    private static void _appendInt(final int bits, final char[] ch, final int offset) {
        _appendShort(bits >> 16, ch, offset);
        _appendShort(bits, ch, offset + 4);
    }
    
    private static void _appendShort(final int bits, final char[] ch, int offset) {
        ch[offset] = UUIDSerializer.HEX_CHARS[bits >> 12 & 0xF];
        ch[++offset] = UUIDSerializer.HEX_CHARS[bits >> 8 & 0xF];
        ch[++offset] = UUIDSerializer.HEX_CHARS[bits >> 4 & 0xF];
        ch[++offset] = UUIDSerializer.HEX_CHARS[bits & 0xF];
    }
    
    private static final byte[] _asBytes(final UUID uuid) {
        final byte[] buffer = new byte[16];
        final long hi = uuid.getMostSignificantBits();
        final long lo = uuid.getLeastSignificantBits();
        _appendInt((int)(hi >> 32), buffer, 0);
        _appendInt((int)hi, buffer, 4);
        _appendInt((int)(lo >> 32), buffer, 8);
        _appendInt((int)lo, buffer, 12);
        return buffer;
    }
    
    private static final void _appendInt(final int value, final byte[] buffer, int offset) {
        buffer[offset] = (byte)(value >> 24);
        buffer[++offset] = (byte)(value >> 16);
        buffer[++offset] = (byte)(value >> 8);
        buffer[++offset] = (byte)value;
    }
    
    static {
        HEX_CHARS = "0123456789abcdef".toCharArray();
    }
}
