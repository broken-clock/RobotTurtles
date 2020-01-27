// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.util.Arrays;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.UUID;

public class UUIDDeserializer extends FromStringDeserializer<UUID>
{
    private static final long serialVersionUID = 1L;
    static final int[] HEX_DIGITS;
    
    public UUIDDeserializer() {
        super(UUID.class);
    }
    
    @Override
    protected UUID _deserialize(final String id, final DeserializationContext ctxt) throws IOException {
        if (id.length() == 36) {
            if (id.charAt(8) != '-' || id.charAt(13) != '-' || id.charAt(18) != '-' || id.charAt(23) != '-') {
                this._badFormat(id, ctxt);
            }
            long l1 = this.intFromChars(id, 0, ctxt);
            l1 <<= 32;
            long l2 = (long)this.shortFromChars(id, 9, ctxt) << 16;
            l2 |= this.shortFromChars(id, 14, ctxt);
            final long hi = l1 + l2;
            final int i1 = this.shortFromChars(id, 19, ctxt) << 16 | this.shortFromChars(id, 24, ctxt);
            l1 = i1;
            l1 <<= 32;
            l2 = this.intFromChars(id, 28, ctxt);
            l2 = l2 << 32 >>> 32;
            final long lo = l1 | l2;
            return new UUID(hi, lo);
        }
        if (id.length() == 24) {
            final byte[] stuff = Base64Variants.getDefaultVariant().decode(id);
            return this._fromBytes(stuff, ctxt);
        }
        return this._badFormat(id, ctxt);
    }
    
    @Override
    protected UUID _deserializeEmbedded(final Object ob, final DeserializationContext ctxt) throws IOException {
        if (ob instanceof byte[]) {
            return this._fromBytes((byte[])ob, ctxt);
        }
        super._deserializeEmbedded(ob, ctxt);
        return null;
    }
    
    private UUID _badFormat(final String uuidStr, final DeserializationContext ctxt) throws IOException {
        return (UUID)ctxt.handleWeirdStringValue(this.handledType(), uuidStr, "UUID has to be represented by standard 36-char representation", new Object[0]);
    }
    
    int intFromChars(final String str, final int index, final DeserializationContext ctxt) throws JsonMappingException {
        return (this.byteFromChars(str, index, ctxt) << 24) + (this.byteFromChars(str, index + 2, ctxt) << 16) + (this.byteFromChars(str, index + 4, ctxt) << 8) + this.byteFromChars(str, index + 6, ctxt);
    }
    
    int shortFromChars(final String str, final int index, final DeserializationContext ctxt) throws JsonMappingException {
        return (this.byteFromChars(str, index, ctxt) << 8) + this.byteFromChars(str, index + 2, ctxt);
    }
    
    int byteFromChars(final String str, final int index, final DeserializationContext ctxt) throws JsonMappingException {
        final char c1 = str.charAt(index);
        final char c2 = str.charAt(index + 1);
        if (c1 <= '\u007f' && c2 <= '\u007f') {
            final int hex = UUIDDeserializer.HEX_DIGITS[c1] << 4 | UUIDDeserializer.HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }
        if (c1 > '\u007f' || UUIDDeserializer.HEX_DIGITS[c1] < 0) {
            return this._badChar(str, index, ctxt, c1);
        }
        return this._badChar(str, index + 1, ctxt, c2);
    }
    
    int _badChar(final String uuidStr, final int index, final DeserializationContext ctxt, final char c) throws JsonMappingException {
        throw ctxt.weirdStringException(uuidStr, this.handledType(), String.format("Non-hex character '%c' (value 0x%s), not valid for UUID String", c, Integer.toHexString(c)));
    }
    
    private UUID _fromBytes(final byte[] bytes, final DeserializationContext ctxt) throws JsonMappingException {
        if (bytes.length != 16) {
            throw InvalidFormatException.from(ctxt.getParser(), "Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes", bytes, this.handledType());
        }
        return new UUID(_long(bytes, 0), _long(bytes, 8));
    }
    
    private static long _long(final byte[] b, final int offset) {
        final long l1 = (long)_int(b, offset) << 32;
        long l2 = _int(b, offset + 4);
        l2 = l2 << 32 >>> 32;
        return l1 | l2;
    }
    
    private static int _int(final byte[] b, final int offset) {
        return b[offset] << 24 | (b[offset + 1] & 0xFF) << 16 | (b[offset + 2] & 0xFF) << 8 | (b[offset + 3] & 0xFF);
    }
    
    static {
        Arrays.fill(HEX_DIGITS = new int[127], -1);
        for (int i = 0; i < 10; ++i) {
            UUIDDeserializer.HEX_DIGITS[48 + i] = i;
        }
        for (int i = 0; i < 6; ++i) {
            UUIDDeserializer.HEX_DIGITS[97 + i] = 10 + i;
            UUIDDeserializer.HEX_DIGITS[65 + i] = 10 + i;
        }
    }
}
