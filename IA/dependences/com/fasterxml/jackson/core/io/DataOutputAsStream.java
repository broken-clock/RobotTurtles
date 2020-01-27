// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;

public class DataOutputAsStream extends OutputStream
{
    protected final DataOutput _output;
    
    public DataOutputAsStream(final DataOutput out) {
        this._output = out;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this._output.write(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this._output.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        this._output.write(b, offset, length);
    }
}
