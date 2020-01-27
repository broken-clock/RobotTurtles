// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.InputStream;

public class ByteBufferBackedInputStream extends InputStream
{
    protected final ByteBuffer _b;
    
    public ByteBufferBackedInputStream(final ByteBuffer buf) {
        this._b = buf;
    }
    
    @Override
    public int available() {
        return this._b.remaining();
    }
    
    @Override
    public int read() throws IOException {
        return this._b.hasRemaining() ? (this._b.get() & 0xFF) : -1;
    }
    
    @Override
    public int read(final byte[] bytes, final int off, int len) throws IOException {
        if (!this._b.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, this._b.remaining());
        this._b.get(bytes, off, len);
        return len;
    }
}
