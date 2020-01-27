// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.io;

import java.io.Reader;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class InputDecorator implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public abstract InputStream decorate(final IOContext p0, final InputStream p1) throws IOException;
    
    public abstract InputStream decorate(final IOContext p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    public DataInput decorate(final IOContext ctxt, final DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public abstract Reader decorate(final IOContext p0, final Reader p1) throws IOException;
}
