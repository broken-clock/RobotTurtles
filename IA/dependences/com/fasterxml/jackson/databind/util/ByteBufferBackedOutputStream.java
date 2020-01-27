// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class ByteBufferBackedOutputStream extends OutputStream
{
    protected final ByteBuffer _b;
    
    public ByteBufferBackedOutputStream(final ByteBuffer buf) {
        this._b = buf;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this._b.put((byte)b);
    }
    
    @Override
    public void write(final byte[] bytes, final int off, final int len) throws IOException {
        this._b.put(bytes, off, len);
    }
}
